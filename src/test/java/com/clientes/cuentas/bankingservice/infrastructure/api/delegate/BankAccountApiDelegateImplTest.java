package com.clientes.cuentas.bankingservice.infrastructure.api.delegate;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.application.usecase.CreateBankAccountForCustomerUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetBankAccountDetailUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.UpdateBankAccountTotalUseCase;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.infrastructure.api.mapper.BankAccountApiMapper;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CreateBankAccountForCustomerRequestDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.UpdateBankAccountTotalRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountApiDelegateImplTest {

  @InjectMocks
  private BankAccountApiDelegateImpl delegate;

  @Mock
  private CreateBankAccountForCustomerUseCase createBankAccountForCustomerUseCase;

  @Mock
  private GetBankAccountDetailUseCase getBankAccountDetailUseCase;

  @Mock
  private UpdateBankAccountTotalUseCase updateBankAccountTotalUseCase;

  @Mock
  private BankAccountApiMapper mapper;

  // =========================================================================
  // createBankAccountForCustomer()
  // =========================================================================

  @Test
  void shouldReturn201CreatedWithLocationHeaderAndMappedBody() {
    String apiId = UUID.randomUUID().toString();
    CreateBankAccountForCustomerRequestDTO requestDTO = new CreateBankAccountForCustomerRequestDTO();
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand();
    BankAccount domainResponse = BankAccount.builder().apiId(apiId).build();
    BankAccountNoCustomerDTO expectedDto = new BankAccountNoCustomerDTO();

    when(mapper.toCommand(requestDTO)).thenReturn(command);
    when(createBankAccountForCustomerUseCase.execute(command)).thenReturn(domainResponse);
    when(mapper.toNoCustomerDto(domainResponse)).thenReturn(expectedDto);

    ResponseEntity<BankAccountNoCustomerDTO> response =
            delegate.createBankAccountForCustomer(requestDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    // Location header must point to the newly created resource
    assertEquals(URI.create("/cuentas/" + apiId), response.getHeaders().getLocation());
    assertSame(expectedDto, response.getBody());

    verify(mapper).toCommand(requestDTO);
    verify(createBankAccountForCustomerUseCase).execute(command);
    verify(mapper).toNoCustomerDto(domainResponse);
    verifyNoMoreInteractions(mapper, createBankAccountForCustomerUseCase,
            getBankAccountDetailUseCase, updateBankAccountTotalUseCase);
  }

  @Test
  void shouldBuildLocationUriFromDomainResponseApiId() {
    String apiId = "fixed-api-id-123";
    CreateBankAccountForCustomerRequestDTO requestDTO = new CreateBankAccountForCustomerRequestDTO();
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand();
    BankAccount domainResponse = BankAccount.builder().apiId(apiId).build();

    when(mapper.toCommand(requestDTO)).thenReturn(command);
    when(createBankAccountForCustomerUseCase.execute(command)).thenReturn(domainResponse);
    when(mapper.toNoCustomerDto(domainResponse)).thenReturn(new BankAccountNoCustomerDTO());

    ResponseEntity<BankAccountNoCustomerDTO> response =
            delegate.createBankAccountForCustomer(requestDTO);

    assertEquals(URI.create("/cuentas/fixed-api-id-123"), response.getHeaders().getLocation());
  }

  // =========================================================================
  // getBankAccountByApiId()
  // =========================================================================

  @Test
  void shouldReturn200OkWithMappedBankAccountDto() {
    UUID apiId = UUID.randomUUID();
    BankAccount domainAccount = BankAccount.builder()
            .apiId(apiId.toString())
            .total(new BigDecimal("750.00"))
            .build();
    BankAccountDTO expectedDto = new BankAccountDTO();

    when(getBankAccountDetailUseCase.execute(apiId)).thenReturn(domainAccount);
    when(mapper.toDto(domainAccount)).thenReturn(expectedDto);

    ResponseEntity<BankAccountDTO> response = delegate.getBankAccountByApiId(apiId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expectedDto, response.getBody());

    verify(getBankAccountDetailUseCase).execute(apiId);
    verify(mapper).toDto(domainAccount);
    verifyNoMoreInteractions(mapper, getBankAccountDetailUseCase,
            createBankAccountForCustomerUseCase, updateBankAccountTotalUseCase);
  }

  @Test
  void shouldPassApiIdDirectlyToUseCase() {
    UUID apiId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    BankAccount domainAccount = BankAccount.builder().apiId(apiId.toString()).build();

    when(getBankAccountDetailUseCase.execute(apiId)).thenReturn(domainAccount);
    when(mapper.toDto(domainAccount)).thenReturn(new BankAccountDTO());

    delegate.getBankAccountByApiId(apiId);

    verify(getBankAccountDetailUseCase).execute(apiId);
  }

  // =========================================================================
  // updateBankAccountTotal()
  // =========================================================================

  @Test
  void shouldReturn204NoContentAfterSuccessfulUpdate() {
    UUID accountApiId = UUID.randomUUID();
    BigDecimal newTotal = new BigDecimal("1200.00");
    UpdateBankAccountTotalRequestDTO requestDTO = new UpdateBankAccountTotalRequestDTO(newTotal);

    ResponseEntity<Void> response = delegate.updateBankAccountTotal(accountApiId, requestDTO);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());

    verify(updateBankAccountTotalUseCase).execute(accountApiId, newTotal);
    verifyNoMoreInteractions(updateBankAccountTotalUseCase,
            createBankAccountForCustomerUseCase, getBankAccountDetailUseCase, mapper);
  }

  @Test
  void shouldExtractTotalFromRequestDtoAndPassItToUseCase() {
    UUID accountApiId = UUID.randomUUID();
    BigDecimal newTotal = new BigDecimal("0.00");
    UpdateBankAccountTotalRequestDTO requestDTO = new UpdateBankAccountTotalRequestDTO(newTotal);

    delegate.updateBankAccountTotal(accountApiId, requestDTO);

    verify(updateBankAccountTotalUseCase).execute(accountApiId, new BigDecimal("0.00"));
  }
}

