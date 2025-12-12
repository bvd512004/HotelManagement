package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Guest;
import com.hsf302.hotelmanagement.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    public GuestResult findOrCreateGuest(String fullName, String phoneNumber, String email) {
        String[] nameParts = fullName.trim().split("\\s+", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : fullName;
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        String guestEmail = (email != null && !email.isEmpty()) ? email : phoneNumber + "@temp.com";
        Optional<Guest> existingGuest = guestRepository.findByEmail(guestEmail);

        Guest guest;
        if (existingGuest.isPresent()) {
            guest = existingGuest.get();
            if (email != null && !email.isEmpty()) {
                guest.setEmail(email);
            }
            guest.setPhoneNumber(phoneNumber);
            guest.setFirstName(firstName);
            guest.setLastName(lastName);
        } else {
            guest = new Guest();
            guest.setFirstName(firstName);
            guest.setLastName(lastName);
            guest.setEmail(guestEmail);
            guest.setPhoneNumber(phoneNumber);
        }

        guest = guestRepository.save(guest);

        String displayName = firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
        boolean hasRealEmail = email != null && !email.isEmpty();

        return new GuestResult(guest, displayName, guestEmail, hasRealEmail);
    }

    public static class GuestResult {
        private final Guest guest;
        private final String displayName;
        private final String emailUsed;
        private final boolean hasRealEmail;

        public GuestResult(Guest guest, String displayName, String emailUsed, boolean hasRealEmail) {
            this.guest = guest;
            this.displayName = displayName;
            this.emailUsed = emailUsed;
            this.hasRealEmail = hasRealEmail;
        }

        public Guest getGuest() {
            return guest;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getEmailUsed() {
            return emailUsed;
        }

        public boolean hasRealEmail() {
            return hasRealEmail;
        }
    }
}

