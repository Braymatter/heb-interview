package com.heb.interviewtask.resources;

import com.heb.interviewtask.vo.ImageVO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/images")
public class ImagesResource {

    @GetMapping
    public CollectionModel<ImageVO> getImages(@RequestParam("objects") Optional<List<String>> objectFilters){
        return null;
    }

    @GetMapping("/{id}")
    public EntityModel<ImageVO> fetchImage(@PathVariable("id") String id){
        return null;
    }

    @PostMapping
    public EntityModel<ImageVO> persistImage(@RequestBody MultipartFile imageUpload){
        return null;
    }
}
