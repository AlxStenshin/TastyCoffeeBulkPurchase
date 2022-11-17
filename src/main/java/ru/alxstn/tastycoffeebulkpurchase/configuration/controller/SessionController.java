package ru.alxstn.tastycoffeebulkpurchase.configuration.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionManagerService;


// ToDo: Remove all logic from session controller, controllers should be simple and stupid.

@Controller
public class SessionController {

    private final SessionManagerService sessionManager;

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
            var result = sessionManager.getAllSessions();
            model.addAttribute("sessions", result);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "sessions";
    }

    @GetMapping(value = "/sessions/new", produces = MediaType.TEXT_HTML_VALUE)
    public String addNewSession(Model model, RedirectAttributes redirectAttributes) {
        if (!sessionManager.activeSessionAvailable()) {
            Session session = new Session();
            model.addAttribute("session", session);
            model.addAttribute("pageTitle", "Create new Session");
            return "session_form";
        }
        else {
            redirectAttributes.addFlashAttribute("message", "Only One Active Session Allowed.\n" +
                    "Please close active session first.");
        }
        return "redirect:/sessions";
    }

    @GetMapping(value = "/sessions/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String editSession(@PathVariable("id") Integer id, Model model) {
        Session session = sessionManager.getSessionById(id);
        model.addAttribute("session", session);
        model.addAttribute("pageTitle", "Edit Session");
        return "session_form";
    }

    @GetMapping(value = "/sessions/close/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String closeSession(@PathVariable("id") Integer id, Model model) {
        Session session = sessionManager.getSessionById(id);
        session.setClosed(true);
        model.addAttribute("session", session);
        model.addAttribute("pageTitle", "Close Session");
        return "session_close_form";
    }

    @PostMapping("/sessions/save")
    public String saveSession(Session session, RedirectAttributes redirectAttributes) {
        try {
            sessionManager.saveSession(session);
            redirectAttributes.addFlashAttribute("message", "The Session has been saved successfully!");
            if (session.isClosed()) {
                sessionManager.closeSession(session);
            }
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }
        return "redirect:/sessions";
    }
}

