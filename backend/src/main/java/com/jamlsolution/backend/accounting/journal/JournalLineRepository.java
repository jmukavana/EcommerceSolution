package com.jamlsolution.backend.accounting.journal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JournalLineRepository extends JpaRepository<JournalLine, String> {

    @Query("""
            SELECT jl.account.id, jl.account.code, jl.account.name, jl.account.type,
                   SUM(jl.debitAmount), SUM(jl.creditAmount)
            FROM JournalLine jl JOIN jl.journalEntry je
            WHERE je.status = 'POSTED'
              AND je.entryDate BETWEEN :from AND :to
            GROUP BY jl.account.id, jl.account.code, jl.account.name, jl.account.type
            """)
    List<Object[]> aggregateByAccount(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
