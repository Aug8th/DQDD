package org.example.service.outdoor;


import org.example.entity.outdoor.OutdoorFood;
import org.example.repository.outdoor.OutdoorFoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutdoorFoodService {

    private final OutdoorFoodRepository outdoorFoodRepository;

    // get all food
    public List<OutdoorFood> getAllFoods() {
        return outdoorFoodRepository.findAll();
    }

    public OutdoorFoodService(OutdoorFoodRepository outdoorFoodRepository) {
        this.outdoorFoodRepository = outdoorFoodRepository;
    }
   //get 6 random foods
    public List<OutdoorFood> getRandom6Foods() {
        return outdoorFoodRepository.findRandom6();
    }

    //get food by wtf ever it is
    public List<OutdoorFood> getFoodsByTheme(Integer themeId) {
        return outdoorFoodRepository.findByThemeId(themeId);
    }

}