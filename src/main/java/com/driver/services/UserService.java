package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        user = userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).get();
        Integer age = user.getAge();

        Subscription subscription = user.getSubscription();

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int count17 = 0;
        int count18 = 0;

        for (WebSeries webSeries: webSeriesList){
            if (webSeries.getSubscriptionType().toString().equalsIgnoreCase(user.getSubscription().toString())) {
                if (webSeries.getAgeLimit() < 18) {
                    count17++;
                } else {
                    count18++;
                }
            }
        }

        if (age < 18) return count17;
        else return count18;
    }


}
