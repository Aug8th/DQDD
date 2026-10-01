package org.example.service.outdoor;


import org.example.entity.outdoor.OutdoorFood;
import org.example.repository.outdoor.OutdoorFoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutdoorFoodService {

    private final OutdoorFoodRepository outdoorFoodRepository;

    public OutdoorFoodService(OutdoorFoodRepository outdoorFoodRepository) {
        this.outdoorFoodRepository = outdoorFoodRepository;
    }

    public List<OutdoorFood> getRandom6Foods() {
        return outdoorFoodRepository.findRandom6();
    }
}