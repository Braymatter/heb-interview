package com.heb.interviewtask.dao;

import com.heb.interviewtask.entities.ImageEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ImageDetectionRepository extends CrudRepository<ImageEntity, String> {
    @Query(value = "SELECT * FROM images", nativeQuery = true)
    public List<ImageEntity> fetchAllImages();

    @Query(value = "SELECT image_checksum, filename\n" +
            "FROM (SELECT i.image_checksum, i.filename, t.name, t.confidence,\n" +
            "             COUNT(*) OVER (PARTITION BY i.image_checksum) as num_tags\n" +
            "      FROM images i JOIN\n" +
            "           tags t\n" +
            "           ON t.image = i.image_checksum\n" +
            "      WHERE t.name in :tags\n" +
            "     ) it\n" +
            "WHERE num_tags = :tagLength", nativeQuery = true)
    public List<ImageEntity> fetchImagesWithTags(@Param("tags") List<String> tags, @Param("tagLength") int numTags);
}
