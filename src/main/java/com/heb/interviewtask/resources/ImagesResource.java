package com.heb.interviewtask.resources;

import com.amazonaws.services.s3.AmazonS3;
import com.heb.interviewtask.entities.ImageEntity;
import com.heb.interviewtask.manager.ObjectDetectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.heb.interviewtask.util.HEBConstants.S3_BUCKET_NAME;

@RestController
@RequestMapping("/images")
public class ImagesResource {
    private ObjectDetectionManager manager;
    private AmazonS3 s3;

    @Autowired
    public ImagesResource(ObjectDetectionManager manager, AmazonS3 s3){
        this.manager = manager;
        this.s3 = s3;
    }

    @GetMapping
    public CollectionModel<EntityModel<ImageEntity>> getImages(@RequestParam("objects") Optional<List<String>> objectFilters, HttpServletRequest req){
        List<ImageEntity> images;
        if(objectFilters.isPresent()){
            images = manager.fetchImagesWithTags(objectFilters.get());
        }else{
            images = manager.fetchAllImages();
        }

        //Fear not o hiring manager, the JVM automatically optimizes this to use StringBuilder (still don't do it in loops):
        //https://medium.com/javarevisited/java-compiler-optimization-for-string-concatenation-7f5237e5e6ed
        String selfLink = req.getRequestURI() + "?" + req.getQueryString();
        List<EntityModel<ImageEntity>> imageModels = images.stream().map(img -> {
            return EntityModel
                    .of(img)
                    .add(Link.of(req.getRequestURI()+"/"+img.getHash(), "self"))
                    .add(Link.of(s3.getUrl(S3_BUCKET_NAME, img.getHash()).toString(), "image"));
        }).collect(Collectors.toList());

        return CollectionModel.of(imageModels).add(Link.of(selfLink,"self"));
    }

    @GetMapping("/{id}")
    public EntityModel<ImageEntity> fetchImage(@PathVariable("id") String id,  HttpServletRequest req){
        ImageEntity entity = manager.fetchImage(id);
        return EntityModel
                .of(entity)
                .add(Link.of(req.getRequestURI(), "self"))
                .add(Link.of(s3.getUrl(S3_BUCKET_NAME, entity.getHash()).toString(), "image"));
    }

    @PostMapping
    public EntityModel<ImageEntity> persistImage(@RequestParam("image") MultipartFile imageFile, HttpServletRequest req){
        ImageEntity processedImage = manager.processNewImage(imageFile);
        return EntityModel
                .of(processedImage)
                .add(Link.of(req.getRequestURI()+"/"+processedImage.getHash(), "self"))
                .add(Link.of(s3.getUrl(S3_BUCKET_NAME, processedImage.getHash()).toString(), "image"));
    }
}
