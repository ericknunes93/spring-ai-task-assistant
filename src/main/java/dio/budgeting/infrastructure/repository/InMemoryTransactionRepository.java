package dio.budgeting.infrastructure.repository;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repositório em memória mantido para isolar o foco do desafio
 * na integração com o Spring AI (Tool Calling, Whisper STT e TTS),
 * sem adicionar complexidade desnecessária de persistência.
 */
@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<Long, Transaction> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.assignId(idSequence.getAndIncrement());
        }
        storage.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return storage.values().stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByCategory(TransactionCategory category) {
        return storage.values().stream()
                .filter(t -> t.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByTypeAndCategory(TransactionType type, TransactionCategory category) {
        return storage.values().stream()
                .filter(t -> t.getType() == type && t.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumByType(TransactionType type) {
        return storage.values().stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
