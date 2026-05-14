package com.bakery.repository;

import com.bakery.entity.CustomCakeBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomCakeBookingRepository extends JpaRepository<CustomCakeBooking, Long>  {

}
