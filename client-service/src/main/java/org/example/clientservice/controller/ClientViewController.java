package org.example.clientservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clientservice.dto.ClientDTO;
import org.example.clientservice.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")

public class ClientViewController {

    private final ClientService service;

    public ClientViewController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", service.findAll());
        return "clients/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("client", new ClientDTO());
        return "clients/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("client") ClientDTO dto,
                         BindingResult result) {
        if (result.hasErrors()) return "clients/form";
        service.create(dto);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("client", service.findById(id));
        return "clients/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("client") ClientDTO dto,
                         BindingResult result) {
        if (result.hasErrors()) return "clients/form";
        service.update(id, dto);
        return "redirect:/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/clients";
    }
}