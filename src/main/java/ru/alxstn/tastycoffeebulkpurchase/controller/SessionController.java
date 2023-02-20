package ru.alxstn.tastycoffeebulkpurchase.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;

@Controller
public class SessionController {

    private final SessionManagerService sessionManager;

    // ToDo: add "action" parameter for save, delete, close, placeOrder with filter endpoints
    public SessionController(SessionManagerService sessionManager) {
        this.sessionManager = sessionManager;
    }

    @GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
    public String getAll() {
        return "redirect:/sessions";
    }

    @GetMapping(value = "/sessions", produces = MediaType.TEXT_HTML_VALUE)
    public String getAllSessions(Model model) {
        try {
            model.addAttribute("sessions", sessionManager.getAllSessions());
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "sessions";
    }

    // ToDo: find the reason why new session creation called after session save
    @GetMapping(value = "/sessions/new", produces = MediaType.TEXT_HTML_VALUE)
    public String addNewSession(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("session", sessionManager.addNewSession());
            model.addAttribute("pageTitle", "Create new Session");
            return "session_form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/sessions";
    }

    @GetMapping(value = "/sessions/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String editSession(@PathVariable("id") Integer id,
                              Model model) {
        model.addAttribute("session", sessionManager.getSessionById(id));
        model.addAttribute("pageTitle", "Edit Session");
        return "session_form";
    }

    @PostMapping("/sessions/save")
    public String saveSession(Session session, RedirectAttributes redirectAttributes) {
        try {
            sessionManager.saveSession(session);
            redirectAttributes.addFlashAttribute("message", "The Session has been saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }
        return "redirect:/sessions";
    }

    @GetMapping(value = "/sessions/{id}/close", produces = MediaType.TEXT_HTML_VALUE)
    public String closeSession(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Session session = sessionManager.getSessionById(id);
        sessionManager.closeSession(session);
        redirectAttributes.addFlashAttribute("message", "Session Closed With Customer Notifications!");
        return "redirect:/sessions";
    }

    @GetMapping(value = "/sessions/{id}/placeOrder/discardedTypes/", produces = MediaType.TEXT_HTML_VALUE)
    public String discardSessionProducts(@PathVariable("id") Integer id,
                                         Model model) {
        model.addAttribute("properties",
                sessionManager.buildDiscardedProductTypes(sessionManager.getSessionById(id)));
        return "session_place_order_form";
    }

    @GetMapping(value = "/sessions/{id}/placeOrder/acceptedTypes/", produces = MediaType.TEXT_HTML_VALUE)
    public String acceptSessionProducts(@PathVariable("id") Integer id,
                                        Model model) {
        model.addAttribute("properties",
                sessionManager.buildAcceptedProductTypes(sessionManager.getSessionById(id)));
        return "session_place_order_form";
    }

    @PostMapping(value = "/sessions/placeOrder/")
    public String placeSessionOrders(SessionProductFilters properties,
                                     RedirectAttributes redirectAttributes) {
        try {
            sessionManager.placeSessionPurchases(properties);
            redirectAttributes.addFlashAttribute("message", "The Orders will be placed now.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }
        return "redirect:/sessions";
    }

}


