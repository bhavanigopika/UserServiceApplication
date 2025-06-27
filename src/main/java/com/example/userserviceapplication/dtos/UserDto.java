package com.example.userserviceapplication.dtos;

import com.example.userserviceapplication.models.Role;
import com.example.userserviceapplication.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    //Return the user information
    private String username;
    private String email;
    //Return the roles that the user has. Return the list of roles to the client
    private List<Role> roleList;
    //private ResponseStatus responseStatus;

    //accepts the user object and convert it into userDTO - we apply abstraction here
    //return type is UserDto. So, create the userDto from  user object and then return it back to the client
    //This method has the responsibility to create userDto from User object (i.e) convert to userDto from user
    //public static UserDto fromUser(User user) {
    //(or)
    public static UserDto from(User user) {
        //Handle it here if the user is null or not
        if(user == null){
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRoleList(user.getRoleList());
        return userDto;

    }
}
