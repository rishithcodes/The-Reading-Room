package com.readingroom;

import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final LibraryService library;
    private final UserRepository users;
    public ProfileController(LibraryService library, UserRepository users){this.library=library;this.users=users;}

    @GetMapping public Map<String,Object> get(@RequestHeader(value="Authorization",required=false) String auth){return view(library.authenticated(auth));}

    @PutMapping public Map<String,Object> update(@RequestHeader(value="Authorization",required=false) String auth,@RequestBody Map<String,String> body){
        User user=library.authenticated(auth);
        String name=body.getOrDefault("fullName","").trim();
        if(name.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name is required");
        user.setFullName(name);user.setPhone(body.getOrDefault("phone",""));user.setDepartment(body.getOrDefault("department",""));user.setStudentId(body.getOrDefault("studentId",""));
        return view(users.save(user));
    }

    @PutMapping("/password") public Map<String,Object> password(@RequestHeader(value="Authorization",required=false) String auth,@RequestBody Map<String,String> body){
        User user=library.authenticated(auth);String current=body.getOrDefault("currentPassword","");String next=body.getOrDefault("newPassword","");
        if(user.getPasswordHash()==null||!BCrypt.checkpw(current,user.getPasswordHash()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Current password is incorrect");
        if(next.length()<8)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"New password must contain at least 8 characters");
        user.setPasswordHash(BCrypt.hashpw(next,BCrypt.gensalt(12)));users.save(user);return Map.of("success",true,"message","Password updated","data",Map.of());
    }
    private Map<String,Object> view(User u){return Map.of("success",true,"message","Profile loaded","data",Map.of("id",u.getId(),"fullName",u.getFullName(),"email",u.getEmail(),"role",u.getRole(),"phone",u.getPhone()==null?"":u.getPhone(),"department",u.getDepartment()==null?"":u.getDepartment(),"studentId",u.getStudentId()==null?"":u.getStudentId()));}
}
