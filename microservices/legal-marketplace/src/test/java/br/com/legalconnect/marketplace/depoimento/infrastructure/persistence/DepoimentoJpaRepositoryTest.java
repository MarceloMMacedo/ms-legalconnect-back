package br.com.legalconnect.marketplace.depoimento.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.legalconnect.marketplace.depoimento.domain.enums.DepoimentoStatus;
import br.com.legalconnect.marketplace.depoimento.domain.enums.TipoDepoimento;
import br.com.legalconnect.marketplace.depoimento.domain.model.Depoimento;

/**
 * Testes de integração para o repositório JPA de Depoimentos.
 * Utiliza @DataJpaTest para carregar apenas os componentes JPA.
 */
@DataJpaTest
@ActiveProfiles("test") // Garante que o perfil de teste seja usado para configurações de banco de dados
public class DepoimentoJpaRepositoryTest {

    @Autowired
    private DepoimentoJpaRepository repository;

    @Test
    @DisplayName("Deve salvar um depoimento com sucesso")
    void deveSalvarDepoimento() {
        Depoimento depoimento = Depoimento.builder()
                .texto("Excelente plataforma e serviço de qualidade!")
                .nome("Maria Oliveira")
                .local("São Paulo, SP")
                .fotoUrl("[https://example.com/foto_maria.jpg](https://example.com/foto_maria.jpg)")
                .userId(UUID.randomUUID()) // Gerar um UUID para o usuário
                .tipoDepoimento(TipoDepoimento.CLIENTE)
                .status(DepoimentoStatus.PENDENTE) // Definindo status
                .build();

        Depoimento salvo = repository.save(depoimento);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getTexto()).isEqualTo("Excelente plataforma e serviço de qualidade!");
        assertThat(salvo.getCreatedAt()).isNotNull();
        assertThat(salvo.getUpdatedAt()).isNotNull();
        assertThat(salvo.getUserId()).isEqualTo(depoimento.getUserId());
        assertThat(salvo.getTipoDepoimento()).isEqualTo(TipoDepoimento.CLIENTE);
        assertThat(salvo.getStatus()).isEqualTo(DepoimentoStatus.PENDENTE);
    }

    @Test
    @DisplayName("Deve encontrar depoimentos aleatórios APROVADOS")
    void deveEncontrarDepoimentosAleatoriosAprovados() {
        // Criar alguns depoimentos, alguns aprovados e outros pendentes
        repository.save(Depoimento.builder().texto("Depoimento 1 (APROVADO)").nome("Pessoa 1").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.APROVADO).build());
        repository.save(Depoimento.builder().texto("Depoimento 2 (PENDENTE)").nome("Pessoa 2").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.PROFISSIONAL).status(DepoimentoStatus.PENDENTE).build());
        repository.save(Depoimento.builder().texto("Depoimento 3 (APROVADO)").nome("Pessoa 3").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.APROVADO).build());
        repository
                .save(Depoimento.builder().texto("Depoimento 4 (REPROVADO)").nome("Pessoa 4").userId(UUID.randomUUID())
                        .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.REPROVADO).build());
        repository.save(Depoimento.builder().texto("Depoimento 5 (APROVADO)").nome("Pessoa 5").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.PROFISSIONAL).status(DepoimentoStatus.APROVADO).build());

        List<Depoimento> depoimentosAleatoriosAprovados = repository.buscarAleatoriosAprovados(2);
        assertThat(depoimentosAleatoriosAprovados).hasSize(2);
        assertThat(depoimentosAleatoriosAprovados).allMatch(d -> d.getStatus() == DepoimentoStatus.APROVADO);
    }

    @Test
    @DisplayName("Deve encontrar os 5 depoimentos APROVADOS mais recentes")
    void deveEncontrarOs5DepoimentosAprovadosMaisRecentes() {
        // Criar depoimentos com datas e status variados
        Depoimento dep1 = Depoimento.builder().texto("Recente APROVADO 1").nome("R1").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.APROVADO).build();
        dep1.setCreatedAt(LocalDateTime.now().minusHours(1));
        repository.save(dep1);

        Depoimento dep2 = Depoimento.builder().texto("Recente PENDENTE 2").nome("R2").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.PENDENTE).build();
        dep2.setCreatedAt(LocalDateTime.now().minusHours(2));
        repository.save(dep2);

        Depoimento dep3 = Depoimento.builder().texto("Recente APROVADO 3").nome("R3").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.PROFISSIONAL).status(DepoimentoStatus.APROVADO).build();
        dep3.setCreatedAt(LocalDateTime.now().minusHours(3));
        repository.save(dep3);

        Depoimento dep4 = Depoimento.builder().texto("Recente APROVADO 4").nome("R4").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.APROVADO).build();
        dep4.setCreatedAt(LocalDateTime.now().minusHours(4));
        repository.save(dep4);

        Depoimento dep5 = Depoimento.builder().texto("Recente REPROVADO 5").nome("R5").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.REPROVADO).build();
        dep5.setCreatedAt(LocalDateTime.now().minusHours(5));
        repository.save(dep5);

        Depoimento dep6 = Depoimento.builder().texto("Recente APROVADO 6").nome("R6").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.PROFISSIONAL).status(DepoimentoStatus.APROVADO).build();
        dep6.setCreatedAt(LocalDateTime.now().minusHours(6));
        repository.save(dep6);

        Depoimento dep7 = Depoimento.builder().texto("Recente APROVADO 7").nome("R7").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.APROVADO).build();
        dep7.setCreatedAt(LocalDateTime.now().minusHours(7));
        repository.save(dep7);

        List<Depoimento> maisRecentesAprovados = repository
                .findTop5ByStatusOrderByCreatedAtDesc(DepoimentoStatus.APROVADO);
        assertThat(maisRecentesAprovados).hasSize(5);
        assertThat(maisRecentesAprovados).allMatch(d -> d.getStatus() == DepoimentoStatus.APROVADO);
        assertThat(maisRecentesAprovados.get(0).getTexto()).contains("Recente APROVADO 1");
        assertThat(maisRecentesAprovados.get(4).getTexto()).contains("Recente APROVADO 7");
    }

    @Test
    @DisplayName("Deve encontrar depoimentos por tipo")
    void deveEncontrarDepoimentosPorTipo() {
        repository.save(Depoimento.builder().texto("Cliente A").nome("A").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.APROVADO).build());
        repository.save(Depoimento.builder().texto("Profissional B").nome("B").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.PROFISSIONAL).status(DepoimentoStatus.PENDENTE).build());
        repository.save(Depoimento.builder().texto("Cliente C").nome("C").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.REPROVADO).build());

        List<Depoimento> depoimentosClientes = repository.findByTipoDepoimento(TipoDepoimento.CLIENTE);
        assertThat(depoimentosClientes).hasSize(2);
        assertThat(depoimentosClientes).extracting(Depoimento::getTexto).containsExactlyInAnyOrder("Cliente A",
                "Cliente C");

        List<Depoimento> depoimentosProfissionais = repository.findByTipoDepoimento(TipoDepoimento.PROFISSIONAL);
        assertThat(depoimentosProfissionais).hasSize(1);
        assertThat(depoimentosProfissionais).extracting(Depoimento::getTexto)
                .containsExactlyInAnyOrder("Profissional B");
    }

    @Test
    @DisplayName("Deve encontrar depoimentos por status")
    void deveEncontrarDepoimentosPorStatus() {
        repository.save(Depoimento.builder().texto("Depoimento Aprovado").nome("AP").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.APROVADO).build());
        repository.save(Depoimento.builder().texto("Depoimento Pendente").nome("PE").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.PROFISSIONAL).status(DepoimentoStatus.PENDENTE).build());
        repository.save(Depoimento.builder().texto("Depoimento Reprovado").nome("RE").userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE).status(DepoimentoStatus.REPROVADO).build());

        List<Depoimento> aprovados = repository.findByStatus(DepoimentoStatus.APROVADO);
        assertThat(aprovados).hasSize(1);
        assertThat(aprovados.get(0).getTexto()).isEqualTo("Depoimento Aprovado");

        List<Depoimento> pendentes = repository.findByStatus(DepoimentoStatus.PENDENTE);
        assertThat(pendentes).hasSize(1);
        assertThat(pendentes.get(0).getTexto()).isEqualTo("Depoimento Pendente");

        List<Depoimento> reprovados = repository.findByStatus(DepoimentoStatus.REPROVADO);
        assertThat(reprovados).hasSize(1);
        assertThat(reprovados.get(0).getTexto()).isEqualTo("Depoimento Reprovado");
    }

    @Test
    @DisplayName("Deve atualizar o status de um depoimento")
    void deveAtualizarStatusDepoimento() {
        Depoimento depoimento = Depoimento.builder()
                .texto("Original")
                .nome("Teste")
                .userId(UUID.randomUUID())
                .tipoDepoimento(TipoDepoimento.CLIENTE)
                .status(DepoimentoStatus.PENDENTE)
                .build();
        Depoimento salvo = repository.save(depoimento);

        Optional<Depoimento> updatedDepoimentoOpt = repository.findById(salvo.getId()).map(dep -> {
            dep.setStatus(DepoimentoStatus.APROVADO);
            return repository.save(dep);
        });

        assertThat(updatedDepoimentoOpt).isPresent();
        assertThat(updatedDepoimentoOpt.get().getStatus()).isEqualTo(DepoimentoStatus.APROVADO);
    }
}