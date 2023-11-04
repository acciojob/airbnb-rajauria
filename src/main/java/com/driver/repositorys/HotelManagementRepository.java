package com.driver.repositorys;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class HotelManagementRepository {

    private HashMap<String,Hotel> hotelDb=new HashMap<>();

    private HashMap<Integer, User> userDB=new HashMap<>();

    private HashMap<String,Booking> bookingDB=new HashMap<>();


    public String addHotel(Hotel hotel) {
        if(hotel==null || hotel.getHotelName()==null || hotelDb.containsKey(hotel.getHotelName())){
            return "FAILURE";
        }

        hotelDb.put(hotel.getHotelName(),hotel);
        return "SUCCESS";
    }


    public Integer addUser(User user) {
        userDB.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        String s1="";
        int mostNumberOfFacilities=0;

        for(var ele:hotelDb.keySet()){
            Hotel hotel=hotelDb.get(ele);
            int numberOfFacility=hotel.getFacilities().size();
            if(mostNumberOfFacilities<numberOfFacility){
                mostNumberOfFacilities=numberOfFacility;
                s1=hotelDb.get(ele).getHotelName();
            }
            else if(mostNumberOfFacilities==numberOfFacility){
                String s2=hotelDb.get(ele).getHotelName();
                int compare=s1.compareTo(s2);
                if(compare>0){
                    s1=s2;
                    mostNumberOfFacilities=numberOfFacility;
                }
            }
        }
        if(mostNumberOfFacilities==0){
            return "";
        }
        return s1;
    }

    public int bookARoom(Booking booking) {
        // Check if the booking or hotel does not exist
        if (booking == null || booking.getHotelName() == null || !hotelDb.containsKey(booking.getHotelName())) {
            return -1;
        }

        // Get the hotel from the database
        Hotel hotel = hotelDb.get(booking.getHotelName());

        // Calculate the total amount paid
        int totalAmountPaid = booking.getNoOfRooms() * hotel.getPricePerNight();

        // Check if there are enough rooms available
        if (booking.getNoOfRooms() > hotel.getAvailableRooms()) {
            return -1;
        }

        // Update the available rooms in the hotel
        hotel.setAvailableRooms(hotel.getAvailableRooms() - booking.getNoOfRooms());

        // Generate a random UUID for the booking
        String bookingId = UUID.randomUUID().toString();
        booking.setBookingId(bookingId);

        // Store the booking in the database
        bookingDB.put(bookingId, booking);

        // Set the amount to be paid in the booking
        booking.setAmountToBePaid(totalAmountPaid);

        return totalAmountPaid;
    }

    public int getBookings(Integer aadharCard) {
        int value=aadharCard.intValue();
        int bookingCount = 0;

        for (Booking booking : bookingDB.values()) {
            if (booking.getBookingAadharCard() == value) {
                bookingCount++;
            }
        }
        return bookingCount;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {

        // Check if the hotel exists in the database
        if (!hotelDb.containsKey(hotelName)) {
            return null; // Hotel doesn't exist
        }

        // Get the hotel from the database
        Hotel hotel = hotelDb.get(hotelName);

        // Get the existing facilities for the hotel
        List<Facility> existingFacilities = hotel.getFacilities();

        // Add new facilities to the existing ones if they are not already present
        for (Facility facility : newFacilities) {
            if (!existingFacilities.contains(facility)) {
                existingFacilities.add(facility);
            }
        }

        // Update the facilities for the hotel
        hotel.setFacilities(existingFacilities);

        // Update the hotel in the database
        hotelDb.put(hotelName, hotel);

        return hotel; // Return the updated hotel
    }
}
