package com.example.userserviceapplication.services;

import com.example.userserviceapplication.dtos.SendEmailDto;
import com.example.userserviceapplication.exceptions.InvalidPasswordException;
import com.example.userserviceapplication.models.Token;
import com.example.userserviceapplication.models.User;
import com.example.userserviceapplication.repositories.TokenRepository;
import com.example.userserviceapplication.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
//Once marked the @Service, spring creates the bean of type UserServiceImpl
//spring created the dependency graph at background. This is like topological sort...
public class UserServiceImpl implements UserService {
    //The following lines we write because we don't need to write new objects every time
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenRepository tokenRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;


    //spring boot not going to create bean for bCryptPasswordEncoder type and make it available during the program execution. So, create bean in configs(See the configs package)
    //now inject the dependency of BCryptPasswordEncoder after creating the bean in AppConfig class
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           TokenRepository tokenRepository,
                           KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
/*
    @Override
    public Token login(String email, String password) throws InvalidPasswordException {
        //Once signup, user try to log in to issue token. Before that check if the user exist or not
        Optional<User> userOptional = userRepository.findByEmail(email);
        //if user does not exist, then throw an exception
        if (userOptional.isEmpty()) {
            //username not found exception available from spring security
            throw new UsernameNotFoundException("User not exist");
            //front end helps to redirect to signup
        }
        //if user found
        User user = userOptional.get();
        *//*once found, verify the password to authenticated
        verify the password which you entered and password in database
        bCryptPasswordEncoder have 2 functions encode(String) and matches(originalString, encodedString). Here, we are using matches because we have to verify and not to create new passowrd
        If I give .encode then new password will create. So, provide .matches*//*
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())){
            *//*
            if it is not matches, then login unsuccessful because this is not a valid user
            throw an exception to invalid password
            or if you try to log in 3 times as wrong, then block the login for 1 hr or 2 hr(some website shows). Not try to attempt for next 1 hr or 2 hr for extra security
            Front end not handle throw an exception. The throw exception handle it by global handler (i.e) @ControllerAdvice (or) controller to handle the exception(if you want)
            This controllerAdvice will send relevant message to client/user. Client will get the response like this
            *//*
            *//*
            {
               response : {
                    success : ok,
                    additionalInformation : "Incorrect password"
               }
            }
            //You can enter the log details(logging mechanism -> DEBUG, WARN, ERROR, INFO, TRACE, FATAL
             *//*
            throw new InvalidPasswordException("The given password is invalid");
        }

        //once password match or valid, then issue the token to the user
        Token token = new Token();
        *//*store the token in your side or client side in the browser cache wherever and use it for subsequent request
        next time when you want to call the product service to fetch the product then use this token and validate your identity and, you don't need to log in again and again
        One of the way to validate the token without even making a call to database is JWT(JSON Web Token)
        Now, we don't use JWT now(Refer notes to learn)

        So, generate random 128 digit token value. Use this dependency "Apache Commons Lang" to get RandomStringUtils
        It creates 128 bit random string*//*
        token.setTokenValue(RandomStringUtils.randomAlphanumeric(128));
        //already check the user and valid. See above...
        token.setUser(user);
        //now set the expiry in 30 days from when it is generated.
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);

        token.setExpiryDateTime(thirtyDaysFromNow);

        //now save the token and return it. Now, when I try to login once user signed up, token gets generatetd

        *//*Token savedToken = tokenRepository.save(token);
        return savedToken;*//*
        //or
        return tokenRepository.save(token);

    }
*/
    //To learn jwt, let's have signUp method only...So, I comment others...
    @Override
    public User signUp(String username, String email, String password) throws JsonProcessingException {
        /*Don't use this here -> BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Create the bean in separate class. See AppConfig class
        1)To signup, first create the user. Then do we need to check user already exist or not? YES
        2) If user already exist, then redirect to login page that is handled by client(front end). In this case, if user already exist, then you return the user object
        3) Otherwise, create the user with the details which you have send and return the user object*/
        Optional<User> userOptional = userRepository.findByEmail(email);
        User savedUser = null;
        //if the user exist, return the existing user object
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        //if the user is not present, create the new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        //now we have instinct of bCryptPasswordEncoder, we should be able to use it to encode the password.
        //password store it in the encoded format
        user.setPassword(bCryptPasswordEncoder.encode(password));

        /*
        First push an event to kafka saying that this is an event whoever wants to consume this event can subscribe to this particular topic which I am going to push
        So, to learn kafka, first push an event to kafka which the EmailService will read and EmailService will send a "welcome email" to the  user
        First connect to kafka, so add dependency in pom.xml then use kafka template.
        -> Kafka template has key and value. Key is topic and Value is Data/Event/Message
        -> User Service talking to instance of Kafka that is running on your system(either different port). Here so send it in a
           string format because it is serialized in string and later deserialize in EmailService
        */
        //kafkaTemplate(which "topic" to send, what "data" to send)
        //If we want to send data over networks, then we need Dto object

        //create sendEmailDto which has to be sent to the email service
        SendEmailDto sendEmailDto = new SendEmailDto();
        //get the email details of user who has signed up and set the email
        sendEmailDto.setEmail(user.getEmail());
        sendEmailDto.setSubject("Welcome to Scaler");
        sendEmailDto.setBody("Happy to have you onboard. Your journey starts today...");
        /*
        now serialize the above particular sendEmailDto data in JSON format, and you convert it into string, and you deserialize it in Email Service
        writValueAsString() method can be used to serialize any Java value as a String format and send it through the kafka
        To serialize and deserialize data in java, we use library called jackson. Jackson provides something called as Object Mapper
        This is the way of userService to push an event to kafka and send email. This is not going to wait and it is asynchronous. This will do the following line and save the user in userRepository then return.
        //JsonProcessingException occurs because of writeValueAsString
        */
        kafkaTemplate.send(
                "sendEmail",
                objectMapper.writeValueAsString(sendEmailDto)
        );


        savedUser = userRepository.save(user);
        return savedUser;
        //or return directly as
        //return userRepository.save(user);

    }

    //authenticate the user if password match or not
    @Override
    public User authenticateUser(String email, String password) {
        //check if the user exist or not. If user not authenticate, then return null
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }
        //check password matches in order to authenticate the user
        User user = userOptional.get();
        //if bcrypt password not match
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        //else bcrypt password matches, then return user
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        //return the user if email found else return null
        return userRepository.findByEmail(email).orElse(null);
    }

    /*

    @Override
    public void logout(String tokenValue) {
        */
/*logout - logout means you can log in again but, you don't sign up till some particular days. Until that you extend the token.
        logout means may be you invalidate the token / close the session / send back to the login page / delete the token not recommended because to back trace
        save all the information(of course, you already saved when you log in actually) to keep backtrack if something wrong

        Just I want to return if the logout is successful or not, so I'll use ResponseEntity - This itself say HTTP status whether it is HTTPStatus.OK (or) NOTFOUND
        Here expire the token, once expired user redirected to login page and, he will ask to log in again (i.e) generated a new token and use this token for their related particular session*//*

    */
/*  Implementation:
        -> set the expiry date of the token to now and invalidating the token
    *//*

        Optional<Token> tokenOptional = tokenRepository.findByTokenValue(tokenValue);
        //logout means already he/she should be in the page. But let's check if the tokenValue is empty or not
        if (tokenOptional.isEmpty()) {
            return;
        }
        //generally delete the token not recommended because to keep back track the user account
        //tokenRepository.delete(tokenOptional.get());

        //So, first get the token
        Token token = tokenOptional.get();

        */
/*You log out. Until the login, extend the expiry date. Currently, the token will be invalidated
        Set the expiry date, then return back to the login page
        don't set the user and make the tokenValue as null -> Not recommended this => token.setTokenValue(null);
        Now,
        let's have a condition...if the user not login again till 1 year, then delete the account*//*


        */
/*
        LocalDateTime now = LocalDateTime.now();//Get the current date and time
        LocalDateTime expiryDateTime = token.getExpiryDateTime();//Get the expiry date and time
        Duration duration = Duration.between(expiryDateTime, now);//calculate time part difference(hours, minutes, seconds)
        Period period = Period.between(expiryDateTime.toLocalDate(), now.toLocalDate());//calculate the date part different(years, months, days)
        long totalSeconds = duration.getSeconds();
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        System.out.printf("Difference: %d years, %d months, %d days, %d hours, %d minutes, %d seconds\n",
                period.getYears(), period.getMonths(), period.getDays(), hours, minutes, seconds);
        *//*


        //Checks if this date-time is before the specified date-time
        if(token.getExpiryDateTime().isBefore(LocalDateTime.now())){
            tokenRepository.delete(token);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);

        token.setExpiryDateTime(thirtyDaysFromNow);
        */
/*Not recommended the following line...
        token.setTokenValue(null);
        token.setUser(null);

        we don't want to make the user as null (i.e) token.setUser(null); because we want user to back trace
        we want to set the expiry date of token from now and invalidate the token.*//*


        tokenRepository.save(token);
    }
*/

/*
    @Override
    public User validateToken(String tokenValue) {
    */
/*
        Implementation:

        -> whenever the validateToken calls, then the token pass then we need to verify the token in the db or not
        -> Check token exists in the tokens table
        -> need to check that the token is not expired
        -> if expired, we redirect the user to login page and ask them to login again
        -> Check the token = if token expiry date > current date (i.e) It should be less than 30 days. That cover in "findByTokenValueAndExpiryDateTimeGreaterThan" in TokenRepository
        -> if not expired, get the token and from the token get the user because through validating the token we can get the user and send back to the client
    *//*


        //token can be null as well. So, better use with optional of token
        Optional<Token> tokenOptional = tokenRepository.findByTokenValueAndExpiryDateTimeGreaterThan(tokenValue, LocalDateTime.now());
        if(tokenOptional.isEmpty()){
            //throw an exception which is redirect the user to login page. I think front end take care this and all.
            // Let us return null right now
            return null;
        }

    */
/*
        Token token = tokenOptional.get();
        return token.getUser();
    *//*

        //(or)
        //avoid tokenOptional.isEmpty in above code then return the following line
    */
/*
        return tokenOptional.map(Token::getUser).orElse(null);
    *//*

        //(or)
        return tokenOptional.get().getUser();
    }
*/

}
