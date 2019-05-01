package com.homebudget.homebudget.controller;

import static com.homebudget.homebudget.utils.Utils.*;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homebudget.homebudget.model.Donation;
import com.homebudget.homebudget.model.Item;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.DonationRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Utils;

@Controller
public class DonationsController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MonthYearRepository monthYearRepository;
	
	@Autowired
	DonationRepository donationRepository;
	
	private User user;

	@RequestMapping(value = "/donations", method = RequestMethod.GET)
	public String donationsList(ModelMap model) {
		
		user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<MonthYear> monthsYears = monthYearRepository.findByUser(user);
		List<Integer> yearsSorted = getYearsSortedDesc(monthsYears);
		List<Integer> months = getMonthsSortedDescForGivenYear(monthsYears, yearsSorted.get(0));
		
		model.put("years", yearsSorted);
		model.put("months", months);
		
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(months.get(0), 
				yearsSorted.get(0), user).get(0);
		
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository, user);
		
		List<Donation> donations = donationRepository.findByMonthYearOrderByDateTimeDesc(monthYear);
		model.put("donations", donations);
		
		// get donation rate from database, if not exist set rate from previous month
		// when current month is first, set 0
		
		return "donations";
	}
	
	@RequestMapping(value = "/add-donation", method = RequestMethod.GET)
	public String addDonationGet(ModelMap model) {
		
		model.addAttribute("donation", new Donation());
		model.put("currentDate", getCurrentWarsawTime());
		
		return "add-donation";
	}
	
	@RequestMapping(value = "/add-donation", method = RequestMethod.POST)
	public String addDonationPost(ModelMap model, @Valid Donation donation, BindingResult result) {
		
		if (result.hasErrors()) {
			return "add-donation";
		}
		
		MonthYear monthYear = Utils.checkAndAddMonthYear(donation.getDateTime(), monthYearRepository, 
														 user);
		
		donation.setMonthYear(monthYear);
		donation.setUser(user);
		donationRepository.save(donation);
		
		return "redirect:/donations";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get-donations-table", method = RequestMethod.POST)
	public @ResponseBody ItemResponse getExpendituresTable(@RequestBody MonthYearRequest monthYearReq) {
		
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(Integer.parseInt(monthYearReq.month), 
				Integer.parseInt(monthYearReq.year), user).get(0);
		
		List<? extends Item> donations = donationRepository.findByMonthYearOrderByDateTimeDesc(
											monthYear);
		return new ItemResponse((List<Item>) donations);
	}
	
}
