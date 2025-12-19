package com.salesianos.triana.ejercicicio16.servicios;

import com.salesianos.triana.ejercicicio16.entidades.Attendee;
import com.salesianos.triana.ejercicicio16.entidades.Event;
import com.salesianos.triana.ejercicicio16.entidades.EventStatus;
import com.salesianos.triana.ejercicicio16.entidades.Ticket;
import com.salesianos.triana.ejercicicio16.repositorios.AttendeeRepository;
import com.salesianos.triana.ejercicicio16.repositorios.EventRepository;
import com.salesianos.triana.ejercicicio16.repositorios.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

@Service @RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;

    public Ticket buyTicket(Ticket ticket) {

        Attendee attendee = attendeeRepository.findById(ticket.getAttendee().getId()).orElseThrow(NoSuchElementException::new);
        Event event = eventRepository.findById(ticket.getEvent().getId()).orElseThrow(NoSuchElementException::new);

        if (event.getStatus() == EventStatus.Cancelled)
            throw new IllegalArgumentException("No se pueden comprar tickets para un evento cancelado.");

        if (event.getStatus() != EventStatus.Published)
            throw new IllegalArgumentException("Solo se pueden comprar tickets para eventos en estado Published.");

        if (attendee.getTickets().stream().anyMatch(t -> t.getEvent() == event && t.getType() == ticket.getType())) {
            throw new IllegalArgumentException("El asistente ya tiene un ticket de este tipo para el evento.");
        }

        Ticket newTicket = Ticket.builder()
                .type(ticket.getType())
                .price(ticket.getPrice())
                .purchasedAt(LocalDateTime.now())
                .qrCode(generateQrCode(ticket))
                .attendee(attendee)
                .event(event)
                .build();

        return ticketRepository.save(newTicket);
    };

    public Set<Ticket> listTicketsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NoSuchElementException::new);
        return event.getTickets();
    }

    private String generateQrCode(Ticket ticket) {
        return "UNIQUE_QR_CODE";
    }

}
