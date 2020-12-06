package com.app.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.cust_excs.ResourceNotFoundException;
import com.app.model.AddressModel;
import com.app.model.HotelModel;
import com.app.model.MenuItemList;
import com.app.repository.HotelRepository;
import com.app.repository.MenuItemRepository;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class HotelController {

	@Autowired
	private HotelRepository hotelRepository;
	
	@Autowired
    private MenuItemRepository menuItemRepository;

	@GetMapping("/hotels")
	public Page<HotelModel> getAllHotels(Pageable pageable) {
		return hotelRepository.findAll(pageable);
	}

	@PostMapping("/hotels")
	public HotelModel createHotel(@Valid @RequestBody HotelModel hotel) {
		return hotelRepository.save(hotel);
	}
	
	@GetMapping("/hotels/{hotelId}")
	public HotelModel getHotelById(@PathVariable Long hotelId) {
		return hotelRepository.findById(hotelId).get();
	}

	@PutMapping("/hotels/{hotelId}")
	public HotelModel updatePost(@PathVariable Long hotelId, @Valid @RequestBody HotelModel hotelRequest) {
		return hotelRepository.findById(hotelId).map(hotel -> {
			hotel.setHotelName(hotelRequest.getHotelName());
			hotel.setMobileNo(hotelRequest.getMobileNo());
			AddressModel reqAdd = hotelRequest.getAddress();
			if(reqAdd != null) {
				hotel.setAddress(hotelRequest.getAddress());
			}
			return hotelRepository.save(hotel);
		}).orElseThrow(() -> new ResourceNotFoundException("HotelId " + hotelId + " not found"));
	}

	@DeleteMapping("/hotels/{hotelId}")
	public ResponseEntity<?> deletePost(@PathVariable Long hotelId) {
		return hotelRepository.findById(hotelId).map(hotel -> {
			hotelRepository.delete(hotel);
			return ResponseEntity.ok().build();
		}).orElseThrow(() -> new ResourceNotFoundException("HotelId " + hotelId + " not found"));
	}
	
	
	@GetMapping("/hotels/{hotelId}/menu")
    public Page<MenuItemList> getAllMenuItemsByHotelId(@PathVariable (value = "hotelId") Long hotelId,
                                                Pageable pageable) {
        return menuItemRepository.findByHotelId(hotelId, pageable);
    }

    @PostMapping("/hotels/{hotelId}/menu")
    public MenuItemList createMenuItem(@PathVariable (value = "hotelId") Long hotelId,
                                 @Valid @RequestBody MenuItemList menuItem) {
        return hotelRepository.findById(hotelId).map(hotel -> {
        	menuItem.setHotel(hotel);
            return menuItemRepository.save(menuItem);
        }).orElseThrow(() -> new ResourceNotFoundException("HotelId " + hotelId + " not found"));
    }

    @PutMapping("/hotels/{hotelId}/menu/{menuId}")
    public MenuItemList updateMenuItem(@PathVariable (value = "hotelId") Long hotelId,
                                 @PathVariable (value = "menuId") Long menuId,
                                 @Valid @RequestBody MenuItemList menuRequest) {
        if(!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("HotelId " + hotelId + " not found");
        }

        return menuItemRepository.findById(menuId).map(menu -> {
            menu.setItemName(menuRequest.getItemName());
            menu.setItemPrice(menuRequest.getItemPrice());
            menu.setAvailable(menuRequest.isAvailable());
            return menuItemRepository.save(menu);
        }).orElseThrow(() -> new ResourceNotFoundException("menuId " + menuId + "not found"));
    }

    @DeleteMapping("/hotels/{hotelId}/menu/{menuId}")
    public ResponseEntity<?> deleteComment(@PathVariable (value = "hotelId") Long hotelId,
            @PathVariable (value = "menuId") Long menuId) {
        return menuItemRepository.findByIdAndHotelId(menuId, hotelId).map(menu -> {
        	menuItemRepository.delete(menu);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with id " + menuId + " and hotelId " + hotelId));
    }

}