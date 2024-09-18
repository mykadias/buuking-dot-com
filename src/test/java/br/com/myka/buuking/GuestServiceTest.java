package br.com.myka.buuking;

import br.com.myka.buuking.converter.GuestConverter;
import br.com.myka.buuking.entity.Guest;
import br.com.myka.buuking.exception.GuestNotFoundException;
import br.com.myka.buuking.model.request.GuestRequest;
import br.com.myka.buuking.model.response.GuestResponse;
import br.com.myka.buuking.repository.GuestRepository;
import br.com.myka.buuking.service.GuestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @InjectMocks
    private GuestService guestService;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestConverter guestConverter;

    @Test
    void findAll_givenEmptyReturn_emptyListIsReturned() {
        // arrange
        when(guestRepository.findAll()).thenReturn(List.of());

        // act
        List<GuestResponse> guests = guestService.getAll();

        // assert
        assertThat(guests).isEmpty();
        verifyNoInteractions(guestConverter);
    }

    @Test
    void findAll_givenNonEmptyList_convertedListIsReturned() {
        // arrange
        Guest guest = new Guest();
        when(guestRepository.findAll()).thenReturn(List.of(guest));
        when(guestConverter.convert(guest)).thenReturn(new GuestResponse());

        // act
        List<GuestResponse> guests = guestService.getAll();

        // assert
        assertThat(guests).hasSize(1);
    }

    @Test
    void getAll_shouldReturnListOfGuestResponses() {
        // arrange
        Guest guest = new Guest(UUID.randomUUID(), "Bruce Wayne");
        GuestResponse guestResponse = new GuestResponse(UUID.randomUUID(), "Bruce Wayne");

        when(guestRepository.findAll()).thenReturn(List.of(guest));
        when(guestConverter.convert(guest)).thenReturn(guestResponse);

        // act
        List<GuestResponse> result = guestService.getAll();

        // assert
        assertThat(result).containsExactly(guestResponse);
        verify(guestRepository).findAll();
        verify(guestConverter).convert(guest);
    }

    @Test
    void save_shouldSaveGuestSuccessfully() {
        // arrange
        GuestRequest guestRequest = new GuestRequest("Lully Díaz");
        Guest guestEntity = new Guest(UUID.randomUUID(), "Lully Díaz");
        GuestResponse guestResponse = new GuestResponse(UUID.randomUUID(), "Lully Díaz");

        when(guestConverter.convert(guestRequest)).thenReturn(guestEntity);
        when(guestRepository.save(guestEntity)).thenReturn(guestEntity);
        when(guestConverter.convert(guestEntity)).thenReturn(guestResponse);

        // act
        GuestResponse result = guestService.save(guestRequest);

        // assert
        assertThat(result).isEqualTo(guestResponse);

        verify(guestRepository).save(guestEntity);
        verify(guestConverter, times(1)).convert(guestRequest);
        verify(guestConverter, times(1)).convert(guestEntity);
    }

    @Test
    void findById_shouldReturnGuestResponse() {
        // arrange
        UUID guestId = UUID.randomUUID();
        Guest guest = new Guest(guestId, "Lois Lane");
        GuestResponse guestResponse = new GuestResponse(guestId, "Lois Lane");

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(guestConverter.convert(guest)).thenReturn(guestResponse);

        // act
        GuestResponse result = guestService.findById(guestId);

        // assert
        assertThat(result).isEqualTo(guestResponse);
        verify(guestRepository).findById(guestId);
        verify(guestConverter).convert(guest);
    }

    @Test
    void findById_shouldReturnNullWhenGuestNotFound() {
        // arrange
        UUID guestId = UUID.randomUUID();
        when(guestRepository.findById(guestId)).thenReturn(Optional.empty());

        // act
        GuestResponse result = guestService.findById(guestId);

        // assert
        assertThat(result).isNull();
        verify(guestRepository).findById(guestId);
        verifyNoInteractions(guestConverter);
    }

    @Test
    void update_shouldUpdateAndReturnGuestResponse() {
        // arrange
        UUID guestId = UUID.randomUUID();
        GuestRequest guestRequest = new GuestRequest("Tony Stark");
        Guest existingGuest = new Guest(guestId, "Wanda Django");
        Guest updatedGuest = new Guest(guestId, "Tony Stark");
        GuestResponse guestResponse = new GuestResponse(guestId, "Tony Stark");


        when(guestRepository.findById(guestId)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> {
            Guest guest = invocation.getArgument(0);

            return guest.getId().equals(guestId) ? updatedGuest : guest;
        });
        when(guestConverter.convert(updatedGuest)).thenReturn(guestResponse);

        // act
        GuestResponse result = guestService.update(guestId, guestRequest);

        // assert
        assertThat(result).isEqualTo(guestResponse);
        verify(guestRepository).findById(guestId);

        ArgumentCaptor<Guest> guestCaptor = ArgumentCaptor.forClass(Guest.class);
        verify(guestRepository).save(guestCaptor.capture());
        Guest savedGuest = guestCaptor.getValue();
        assertThat(savedGuest.getName()).isEqualTo("Tony Stark");
        assertThat(savedGuest.getId()).isEqualTo(guestId);

        verify(guestConverter).convert(updatedGuest);
    }

    @Test
    void update_shouldThrowGuestNotFoundExceptionWhenGuestNotFound() {
        // arrange
        UUID guestId = UUID.randomUUID();
        GuestRequest guestRequest = new GuestRequest("Deadpool");

        when(guestRepository.findById(guestId)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> guestService.update(guestId, guestRequest))
            .isInstanceOf(GuestNotFoundException.class)
            .hasMessageContaining(guestId.toString());

        verify(guestRepository).findById(guestId);
        verifyNoInteractions(guestConverter);
    }

    @Test
    void delete_shouldDeleteGuestById() {
        // arrange
        UUID guestId = UUID.randomUUID();

        // act
        guestService.delete(guestId);

        // assert
        verify(guestRepository).deleteById(guestId);
        verifyNoInteractions(guestConverter);
    }

    @Test
    void getGuest_shouldReturnGuest() {
        // arrange
        UUID guestId = UUID.randomUUID();
        Guest guest = new Guest(guestId, "Thor Odinson");

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));

        // act
        Guest result = guestService.getGuest(guestId);

        // assert
        assertThat(result).isEqualTo(guest);
        verify(guestRepository).findById(guestId);
    }

    @Test
    void getGuest_shouldThrowGuestNotFoundExceptionWhenGuestNotFound() {
        // arrange
        UUID guestId = UUID.randomUUID();

        when(guestRepository.findById(guestId)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> guestService.getGuest(guestId))
            .isInstanceOf(GuestNotFoundException.class)
            .hasMessageContaining(guestId.toString());

        verify(guestRepository).findById(guestId);
    }
}