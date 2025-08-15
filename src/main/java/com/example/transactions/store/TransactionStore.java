package com.example.transactions.store;

import com.example.transactions.stream.TransactionEvent;

import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionStore {

    private final Map<String, List<TransactionEvent>> byUserMonth = new ConcurrentHashMap<>();

    private static String key(String userId, YearMonth ym) {
        return userId + "::" + ym;
    }

    public void add(TransactionEvent e) {
        YearMonth ym = YearMonth.from(e.valueDate());
        String k = key(e.customerId(), ym);
        byUserMonth.compute(k, (__, list) -> {
            List<TransactionEvent> out = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
            out.add(e);
            out.sort(Comparator.comparing(TransactionEvent::valueDate).thenComparing(TransactionEvent::id));
            return out;
        });
    }

    public List<TransactionEvent> page(String userId, YearMonth ym, String accountIban, int page, int size) {
        var list = Optional.ofNullable(byUserMonth.get(key(userId, ym))).orElse(List.of());
        var filtered = (accountIban == null || accountIban.isBlank())
                ? list
                : list.stream().filter(e -> accountIban.equals(e.accountIban())).collect(Collectors.toList());

        int from = Math.max(0, page * size);
        if (from >= filtered.size()) return List.of();
        int to = Math.min(filtered.size(), from + size);
        return filtered.subList(from, to);
    }

    public boolean hasNext(String userId, YearMonth ym, String accountIban, int page, int size) {
        var list = Optional.ofNullable(byUserMonth.get(key(userId, ym))).orElse(List.of());
        long count = (accountIban == null || accountIban.isBlank())
                ? list.size()
                : list.stream().filter(e -> accountIban.equals(e.accountIban())).count();
        return (long) (page + 1) * size < count;
    }

    public boolean hasData(String userId, YearMonth ym) {
        return byUserMonth.containsKey(key(userId, ym));
    }
}
