package br.com.myka.buuking.service;

import br.com.myka.buuking.converter.GuestConverter;
import br.com.myka.buuking.entity.Guest;
import br.com.myka.buuking.exception.GuestNotFoundException;
import br.com.myka.buuking.model.request.GuestRequest;
import br.com.myka.buuking.model.response.GuestResponse;
import br.com.myka.buuking.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final GuestConverter guestConverter;

    public List<GuestResponse> getAll() {
        return guestRepository.findAll().stream().map(guestConverter::convert).toList();
    }

    public GuestResponse save(GuestRequest guestRequest) {
        return guestConverter.convert(guestRepository.save(guestConverter.convert(guestRequest)));
    }

    public GuestResponse findById(UUID id) {
        return guestRepository.findById(id).map(guestConverter::convert).orElse(null);
    }

    public GuestResponse update(UUID id, GuestRequest guestRequest) {
        var guest = guestRepository.findById(id).orElseThrow(() -> new GuestNotFoundException(id));
        guest.setName(guestRequest.getName());
        return guestConverter.convert(guestRepository.save(guest));
    }

    public void delete(UUID id) {
        guestRepository.deleteById(id);
    }

    public Guest getGuest(UUID guestId) {
        return guestRepository.findById(guestId).orElseThrow(() -> new GuestNotFoundException(guestId));
    }
}
