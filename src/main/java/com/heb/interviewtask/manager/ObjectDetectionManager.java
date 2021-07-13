package com.heb.interviewtask.manager;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.heb.interviewtask.dao.ImageDetectionRepository;
import com.heb.interviewtask.entities.ImageEntity;
import com.heb.interviewtask.entities.TagEntity;
import com.heb.interviewtask.services.ObjectDetectionService;
import com.heb.interviewtask.vo.DetectionResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.heb.interviewtask.util.HEBConstants.HASHING_ALG;
import static com.heb.interviewtask.util.HEBConstants.S3_BUCKET_NAME;

@NoArgsConstructor
@Data
@Slf4j
@Component
@Transactional
public class ObjectDetectionManager {
    private EntityManager em;
    private ObjectDetectionService detectionService;
    private AmazonS3 s3;
    private ImageDetectionRepository detectionRepo;

    @Autowired
    public ObjectDetectionManager(EntityManager em, ObjectDetectionService detectionService, AmazonS3 s3, ImageDetectionRepository repo){
        this.s3 = s3;
        this.detectionRepo = repo;
        this.em = em;
        this.detectionService = detectionService;
    }

    public List<ImageEntity> fetchImagesWithTags(List<String> tags){
        return detectionRepo.fetchImagesWithTags(tags, tags.size());
    }

    public List<ImageEntity> fetchAllImages(){
        return detectionRepo.fetchAllImages();
    }

    public ImageEntity fetchImage(String id){
        return em.find(ImageEntity.class, id);
    }

    public ImageEntity processNewImage(MultipartFile image){

        //Hash the image and see if we already have it in our db, we're going to assume we live in an
        //We could also use optionals here, but since we're going to abort the req if we fail, no point
        MessageDigest md = null;
        String checksum = null;

        try {
            md = MessageDigest.getInstance(HASHING_ALG);
            md.update(image.getBytes());
            checksum = DatatypeConverter.printHexBinary(md.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error(String.format("[persistNewImage] No such algorithm: %s", HASHING_ALG));
            e.printStackTrace();

            //This is a nice out of the box error handler from spring, for a bigger project we may want to
            //Use @ControllerAdvice/@ExceptionHandlers to gain some more control.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error Processing Image");
        } catch(IOException io){
            log.error(String.format("[persistNewImage] IO Exception for file: %s", image.getOriginalFilename()));
            io.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error Processing Image");
        }

        //If we already have an image with that checksum let's just return that
        Optional<ImageEntity> processedImage = Optional.ofNullable(em.find(ImageEntity.class, checksum));

        //Can't use persistNewImage in a .orElse on the optional because it
        //doesn't meet the requirements for a Supplier/functional interface
        if(processedImage.isPresent()){
            return processedImage.get();
        }else{
            return persistNewImage(image, checksum);
        }
    }

    public ImageEntity persistNewImage(MultipartFile image, String checksum){
        //Persist to S3
        try {
            ObjectMetadata metadata = new ObjectMetadata();

            if(image.getOriginalFilename().endsWith(".png")){
                metadata.setContentType("image/png");
            }else if(image.getOriginalFilename().endsWith(".jpeg") || image.getOriginalFilename().endsWith(".jpg")){
                metadata.setContentType("image/jpeg");
            }

            metadata.setContentLength(image.getBytes().length);

            s3.putObject(new PutObjectRequest(S3_BUCKET_NAME, checksum, image.getInputStream(), metadata).withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            log.error(String.format("[persistNewImage] Error getting bytes/input stream from file with name: %s", image.getOriginalFilename()));
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

        //Make call to detection service
        List<DetectionResult> detectedObjects = detectionService.findImageObjects(s3.getUrl(S3_BUCKET_NAME, checksum));
        List<TagEntity> entities = detectedObjects.stream().map(detectedObject -> {
            return new TagEntity(detectedObject.getName(), detectedObject.getConfidence(), checksum);
        }).collect(Collectors.toList());

        //Persist image/detected objects to db if over some threshold.
        ImageEntity img = new ImageEntity(checksum, image.getOriginalFilename(), entities);

        em.persist(img);

        //Return the image entity so it can be mapped out to the client
        return img;
    }
}
