package org.example.service.outdoor;


import lombok.RequiredArgsConstructor;
import org.example.entity.OutdoorFood;
import org.example.repository.OutdoorFoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OutdoorFoodService {

    private final OutdoorFoodRepository outdoorFoodRepository;

    // get all food
    public List<OutdoorFood> getAllFoods() {
        return outdoorFoodRepository.findAll();
    }

   //get 6 random foods
    public List<OutdoorFood> getRandom6Foods() {
        return outdoorFoodRepository.findRandom6();
    }

    //get food by theme
    public List<OutdoorFood> getFoodsByTheme(Integer themeId) {
        return outdoorFoodRepository.findByThemeId(themeId);
    }

}