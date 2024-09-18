package br.com.myka.buuking;

import br.com.myka.buuking.converter.PropertyConverter;
import br.com.myka.buuking.entity.Property;
import br.com.myka.buuking.exception.ValidationException;
import br.com.myka.buuking.model.request.PropertyRequest;
import br.com.myka.buuking.model.response.PropertyResponse;
import br.com.myka.buuking.repository.PropertyRepository;
import br.com.myka.buuking.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @InjectMocks
    private PropertyService propertyService;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyConverter propertyConverter;


    @Test
    void getAll_shouldReturnEmptyListWhenNoProperties() {
        // arrange
        when(propertyRepository.findAll()).thenReturn(List.of());

        // act
        List<PropertyResponse> properties = propertyService.getAll();

        // assert
        assertThat(properties).isEmpty();
        verifyNoInteractions(propertyConverter);
    }

    @Test
    void getAll_shouldReturnAllProperties() {
        // arrange
        UUID propertyId = UUID.randomUUID();;
        Property property = new Property(propertyId,"Avengers Compound Hotel", List.of());
        PropertyResponse propertyResponse = new PropertyResponse(propertyId, "Avengers Compound Hotel",List.of());

        when(propertyRepository.findAll()).thenReturn(List.of(property));
        when(propertyConverter.convert(property)).thenReturn(propertyResponse);

        // act
        List<PropertyResponse> result = propertyService.getAll();

        // assert
        assertThat(result).containsExactly(propertyResponse);
    }


    @Test
    void save_shouldThrowValidationExceptionIfPropertyNameExists() {
        // arrange
        PropertyRequest request = new PropertyRequest("Stark Tower Hotel ");
        UUID propertyId = UUID.randomUUID();
        Property property = new Property(propertyId, "Stark Tower Hotel", List.of());
        when(propertyConverter.convert(request)).thenReturn(property);
        when(propertyRepository.findAllByNameIgnoreCase("Stark Tower Hotel")).thenReturn(List.of(property));

        // act & assert
        assertThatThrownBy(() -> propertyService.save(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("The name Stark Tower Hotel for property already exists");

        verify(propertyRepository).findAllByNameIgnoreCase("Stark Tower Hotel");
    }

    @Test
    void save_shouldSavePropertySuccessfully() {
        // arrange
        UUID propertyId = UUID.randomUUID();
        PropertyRequest propertyRequest = new PropertyRequest("Wakanda Hotel");
        Property property = new Property(propertyId, "Wakanda Hotel", List.of());
        PropertyResponse propertyResponse = new PropertyResponse(propertyId, "Wakanda Hotel", List.of());

        when(propertyConverter.convert(propertyRequest)).thenReturn(property);
        when(propertyRepository.findAllByNameIgnoreCase(property.getName())).thenReturn(List.of());
        when(propertyRepository.save(property)).thenReturn(property);
        when(propertyConverter.convert(property)).thenReturn(propertyResponse);

        // act
        PropertyResponse result = propertyService.save(propertyRequest);

        // assert
        assertThat(result).isEqualTo(propertyResponse);
        verify(propertyRepository).save(property);
    }

    @Test
    void findById_shouldReturnPropertyResponse() {
        // arrange
        UUID propertyId = UUID.randomUUID();
        Property property = new Property(propertyId, "Asgard Hotel", List.of());
        PropertyResponse propertyResponse = new PropertyResponse(propertyId, "Asgard Hotel", List.of());

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(propertyConverter.convert(property)).thenReturn(propertyResponse);

        // act
        PropertyResponse result = propertyService.findById(propertyId);

        // assert
        assertThat(result).isEqualTo(propertyResponse);
    }

    @Test
    void findById_shouldReturnNullIfPropertyNotFound() {
        // arrange
        UUID propertyId = UUID.randomUUID();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // act
        PropertyResponse result = propertyService.findById(propertyId);

        // assert
        assertThat(result).isNull();
    }

    @Test
    void update_shouldThrowValidationExceptionIfUpdatedNameExists() {
        // arrange
        UUID propertyId = UUID.randomUUID();
        PropertyRequest propertyRequest = new PropertyRequest("Batcave Hotel");
        Property existingProperty = new Property(propertyId, "Titans Tower Hotel", List.of());
        Property updatedProperty = new Property(propertyId, "Batcave Hotel", List.of());

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(existingProperty));
        when(propertyRepository.findAllByNameIgnoreCase(updatedProperty.getName())).thenReturn(List.of(new Property(UUID.randomUUID(), "Batcave Hotel", List.of())));

        // act & assert
        assertThatThrownBy(() -> propertyService.update(propertyId, propertyRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("The name Batcave Hotel for property already exists");
    }

    @Test
    void delete_shouldDeleteProperty() {
        // arrange
        UUID propertyId = UUID.randomUUID();

        // act
        propertyService.delete(propertyId);

        // assert
        verify(propertyRepository).deleteById(propertyId);
    }

    @Test
    void getProperty_shouldReturnProperty() {
        // arrange
        UUID propertyId = UUID.randomUUID();
        Property property = new Property(propertyId, "Wayne Manor", List.of());

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // act
        Property result = propertyService.getProperty(propertyId);

        // assert
        assertThat(result).isEqualTo(property);
    }


    @Test
    void findAll_givenNonEmptyList_convertedListIsReturned() {
        // arrange
        Property property = new Property();
        when(propertyRepository.findAll()).thenReturn(List.of(property));
        when(propertyConverter.convert(property)).thenReturn(new PropertyResponse());

        // act
        List<PropertyResponse> guests = propertyService.getAll();

        // assert
        assertThat(guests).hasSize(1);
    }

}