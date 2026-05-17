package com.bakery.repository;

import com.bakery.model.CustomCakeBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomCakeBookingRepository extends JpaRepository<CustomCakeBooking, Long>  {

}
