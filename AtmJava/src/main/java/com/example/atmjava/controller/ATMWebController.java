package com.example.atmjava.controller;

import com.example.atmjava.model.AppUser;
import com.example.atmjava.service.ATMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ATMWebController {

    @Autowired
    private ATMService atmService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/create")
    public String createForm() {
        return "create";
    }

    @PostMapping("/create")
    public String createUser(
            @RequestParam String name,
            @RequestParam String pin,
            Model model) {

        var newUser = atmService.createUser(name, pin);

        model.addAttribute("userId", newUser.getUserID());
        model.addAttribute("accNo", newUser.getAccountNumber());

        return "create_success";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }


    // NOTE: No POST /login here — Spring Security handles authentication POST.

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal,
                            @ModelAttribute("success") String success,
                            @ModelAttribute("error") String error) {
        // Add userId if logged in (principal name is userID)
        if (principal != null) {
            model.addAttribute("userId", principal.getName());
        }
        // flash attrs (if present) are automatically available in model via RedirectAttributes
        if (success != null && !success.isBlank()) model.addAttribute("success", success);
        if (error != null && !error.isBlank()) model.addAttribute("error", error);
        return "dashboard";
    }

    @GetMapping("/balance")
    public String balancePage(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Not authenticated. Please login.");
            return "redirect:/login";
        }
        AppUser u = atmService.getUserById(principal.getName());
        model.addAttribute("balance", u.getBalance());
        model.addAttribute("userId", u.getUserID());
        return "balance";
    }

    @GetMapping("/deposit")
    public String depositForm() {
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam double amount,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Not authenticated. Please login.");
            return "redirect:/login";
        }
        try {
            atmService.deposit(principal.getName(), amount);
            redirectAttributes.addFlashAttribute("success", "Amount deposited successfully: " + amount);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Deposit failed: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/withdraw")
    public String withdrawForm() {
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam double amount,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Not authenticated. Please login.");
            return "redirect:/login";
        }

        try {
            boolean ok = atmService.withdraw(principal.getName(), amount);
            if (!ok) {
                redirectAttributes.addFlashAttribute("error", "Insufficient balance for withdrawal.");
            } else {
                redirectAttributes.addFlashAttribute("success", "Withdrawal successful: " + amount);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Withdrawal failed: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/transfer")
    public String transferForm() {
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String targetAccount,
            @RequestParam double amount,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Not authenticated. Please login.");
            return "redirect:/login";
        }

        try {
            boolean ok = atmService.transfer(principal.getName(), targetAccount, amount);
            if (!ok) {
                redirectAttributes.addFlashAttribute("error", "Transfer failed. Check account number / balance.");
            } else {
                redirectAttributes.addFlashAttribute("success", "Transfer successful: " + amount + " → " + targetAccount);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Transfer failed: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }
}
