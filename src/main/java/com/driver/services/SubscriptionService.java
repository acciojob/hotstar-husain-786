package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        int amountToPaid = 0;

        int fixedPrice = 0, price = 0;
        if (subscriptionEntryDto.getSubscriptionType().toString().equalsIgnoreCase(SubscriptionType.BASIC.toString())){
            fixedPrice = 200;
            price = 500;
        }
        else if (subscriptionEntryDto.getSubscriptionType().toString().equalsIgnoreCase(SubscriptionType.PRO.toString())){
            fixedPrice = 250;
            price = 800;
        }
        else if (subscriptionEntryDto.getSubscriptionType().toString().equalsIgnoreCase(SubscriptionType.ELITE.toString())){
            fixedPrice = 350;
            price = 1000;
        }

        amountToPaid = price + (fixedPrice*subscriptionEntryDto.getNoOfScreensRequired());

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();

        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setTotalAmountPaid(amountToPaid);
        subscription.setUser(user);

        subscriptionRepository.save(subscription);

        return amountToPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();

        Date subscDate = user.getSubscription().getStartSubscriptionDate();
//        int d1 = subscDate.getDay();
        Date todayDate = new Date();
//        int d2 = new Date().getDay();
        int diff = Math.toIntExact(ChronoUnit.DAYS.between((Temporal) subscDate, (Temporal) todayDate));

        int price = 0;
        if (user.getSubscription().toString().equalsIgnoreCase(SubscriptionType.BASIC.toString())) price = 500;
        else if (user.getSubscription().toString().equalsIgnoreCase(SubscriptionType.PRO.toString())) price = 800;
        else if (user.getSubscription().toString().equalsIgnoreCase(SubscriptionType.ELITE.toString())) price = 1000;

        Integer remainingPrice = diff*(price/30);

        int dayRemainingForPro = remainingPrice/1000;
        Integer amountToPay = (30-dayRemainingForPro)*1000;

        Subscription subscription = user.getSubscription();
        subscription.setSubscriptionType(SubscriptionType.ELITE);
        subscription = subscriptionRepository.save(subscription);

        user.setSubscription(subscription);

        userRepository.save(user);

        return amountToPay;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        Integer totalRevenue = 0;
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        for (Subscription subscription: subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
