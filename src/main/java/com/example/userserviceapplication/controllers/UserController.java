
package com.example.userserviceapplication.controllers;

import com.example.userserviceapplication.dtos.LoginRequestDto;
import com.example.userserviceapplication.dtos.LoginResponseDto;
import com.example.userserviceapplication.dtos.LogoutRequestDto;
import com.example.userserviceapplication.dtos.ResponseStatus;
import com.example.userserviceapplication.dtos.SignUpRequestDto;
import com.example.userserviceapplication.dtos.UserDto;
import com.example.userserviceapplication.exceptions.InvalidPasswordException;
import com.example.userserviceapplication.models.Token;
import com.example.userserviceapplication.models.User;
import com.example.userserviceapplication.repositories.TokenRepository;
import com.example.userserviceapplication.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//This class is going to operate on user - So, let's call UserController
@RestController
@RequestMapping("/users")
public class UserController {
    private final TokenRepository tokenRepository;
    private UserService userService;

    public UserController(UserService userService, TokenRepository tokenRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

//    use 4 APIs here login, signup, logout, validate Token
//    Why postMapping? "For new resource creation" - This is important...Whenever you create a new resource then use PostMapping.
//    Because we are going to send the details to post and also, POST creates a new entry in the database.
//    @PostMapping (i.e) in POST - does not about @RequestBody. Even in @GetMapping, I can use @RequestBody
//    Update - you use PUT and PATCH for updates
//    GET - fetch a resource
//    Login is going to POST. Whenever you create a new resource then use PostMapping. Here, for login, you create a new resource (i.e) new token

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) throws InvalidPasswordException {
        LoginResponseDto loginResponseDto = new LoginResponseDto();

        //mostly token cannot be null because in user service we handle the token value to generate as Random Alphanumeric. Refer in userService class
        //suppose getting the token calling from other service, then that service is returning a null... In this case I would have to handle the edge case in userService may be.
        try {
            Token token = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            loginResponseDto.setTokenValue(token.getTokenValue());
            //In response, we won't get password otherwise easy to hack
            //loginResponseDto.setPassword(token.getUser().getPassword());
            loginResponseDto.setResponseStatus(ResponseStatus.SUCCESS);
        }catch(Exception e) {
            loginResponseDto.setResponseStatus(ResponseStatus.FAILURE);
        }

        return loginResponseDto;
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
    /*
        //Return the user information in SignUpResponseDto
        UserDto signUpResponseDto = new UserDto();
        try {
            User user = userService.signUp(signUpRequestDto.getUsername(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword());
            signUpResponseDto.setUsername(user.getUsername());
            signUpResponseDto.setEmail(user.getEmail());
            signUpResponseDto.setResponseStatus(ResponseStatus.SUCCESS);
        }
        catch (Exception e) {
            signUpResponseDto.setResponseStatus(ResponseStatus.FAILURE);
        }
        return signUpResponseDto;
    */
        /*Let's upgrade the program
        Instead of set the username, email, responseStatus in userDto in userController class, set it everything in userDto class
        Also, if user is null, in that case, let's handle it in UserDto class*/
        User user = userService.signUp(signUpRequestDto.getUsername(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword());
        /*return type is UserDto. So, create the userDto from  user object and then return it back to the client
        Converting from user to UserDto and return it back to the Client...*/
        return UserDto.from(user);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        /* logout - logout means you invalidate the token / close the session / send back to the login page / delete the token not recommended because you to back trace
        save all the information(of course, you already saved when you log in actually) to keep backtrack if something wrong*/

        /*Just I want to return if the logout is successful or not, so I'll use ResponseEntity - This itself say HTTP status whether it is HTTPStatus.OK (or) NOTFOUND
        Here expire the token, once expired user redirected to login page and, he will ask to log in again (i.e) generated a new token and use this token for their related particular session*/
        userService.logout(logoutRequestDto.getTokenValue());
        /*Use any return statement(see below), but understand the difference.

        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
        return ResponseEntity.noContent().build();
        return new ResponseEntity<>(HttpStatus.OK);*/
        return ResponseEntity.ok("Expiry date is changed when logged out. It will be current expiry date and please check in token database table");//Here also, HTTP status is 200 OK


    }

    @GetMapping("/validate/{token}")//For validate the token, provide the token actually
    //Now, pass the parameter as @PathVariable
    public ResponseEntity<UserDto> validateToken(@PathVariable("token") String tokenValue){
        /*Return the ResponseEntity with UserDto
        if token is validated then return the particular user
        return type is UserDto. So, create the userDto from  user object and then return it back to the client
        Converting from user to UserDto and return it back to the Client...*/
        User user = userService.validateToken(tokenValue);
        if(user == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);//resource not found. NOT_FOUND = 404
        }
        return new ResponseEntity<>(UserDto.from(user), HttpStatus.OK);//OK = 200
    }

}
