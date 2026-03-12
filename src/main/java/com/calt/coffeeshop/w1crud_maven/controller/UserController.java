package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.request.UserRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.enums.StatusCode;
import com.calt.coffeeshop.w1crud_maven.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
//@CrossOrigin("http://localhost:xxx")//let domain http://localhost:xxx access resut of api to avoid CORS
@Tag(name = "User Controller", description = "")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("")
    //return String becase the whole html site are Strings!!!
    public ApiResponse<List<User>> getUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending){
        var authenication = SecurityContextHolder.getContext().getAuthentication();
        authenication.getAuthorities().forEach(e->
                log.info(e.getAuthority())
                );
        Sort sort= ascending ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,pageSize,sort);
        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setResult(userService.getAllUser(pageable));
        apiResponse.setCode(703);
        apiResponse.setMessage("GOT!");

        return apiResponse;
    }
    @GetMapping("/{username}")
    //return String becase the whole html site are Strings!!!
    public ApiResponse<User> getUser(@PathVariable("username") String username){

        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setResult(userService.getUserByUsername(username));
        apiResponse.setCode(703);
        apiResponse.setMessage("GOT!");

        return apiResponse;
    }



    @PatchMapping("/{username}")
    public ApiResponse<User> updateUser(@PathVariable("username") String username, @RequestBody UserRequest userRequest){
        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setCode(StatusCode.UPDATED.getCode());
        apiResponse.setMessage(StatusCode.UPDATED.getMessage());
        userRequest.setUpdated_at(Instant.now());
        apiResponse.setResult(userService.updateUser(username,userRequest));
        return apiResponse;
    }
    @PostMapping("")
    public ApiResponse<User> addUser(@RequestBody @Valid UserRequest userRequest){
        ApiResponse apiResponse = ApiResponse.builder().build();
        userRequest.setCreated_at(Instant.now());
        userRequest.setUpdated_at(Instant.now());
        apiResponse.setResult( userService.saveUserfromDTO(userRequest));
            return apiResponse;
    }

    @DeleteMapping("/{username}")
    public ApiResponse<String> deleteUser(@PathVariable("username") String username){

        ApiResponse apiResponse = ApiResponse.builder().build();
        userService.deleteUser(userService.getUserByUsername(username));
        apiResponse.setMessage(StatusCode.DELETED.getMessage());
        apiResponse.setCode(StatusCode.DELETED.getCode());
        apiResponse.setResult(null);
        return apiResponse;
    }
}
