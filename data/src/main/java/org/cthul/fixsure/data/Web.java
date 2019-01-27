package org.cthul.fixsure.data;

import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.generators.value.ItemsSequence;

/**
 *
 */
public class Web {

    private Web() {
    }
    
    /**
     * Returns a sequence of three elements: 
     * {@code "example.com"}, {@code "example.net"}, and {@code "example.org"}.
     * @return domains
     */
    public static FlSequence<String> domains() {
        return SEQ_DOMAINS;
    }

    /**
     * Returns a sequence of 7019142 unique elements in the form
     * {@code <Fruit><5-digit int>.example.<com|net|org>}.
     * @return more domains
     */
    public static FlSequence<String> moreDomains() {
        return SEQ_MORE_DOMAINS;
    }
    
    /**
     * Returns an unbounded sequence of email addresses in the form
     * {@code <firstName>.<lastName>@example.<com|net|org>}.
     * @return email addresses
     */
    public static FlSequence<String> emailAddresses() {
        return SEQ_EMAILS;
    }
    
    public static UrlGenerator.Template urls() {
        return UrlGenerator.urls();
    }
    
    private static final String[] DOMAINS = {"example.com", "example.net", "example.org"};
    private static final FlSequence<String> SEQ_DOMAINS = ItemsSequence.sequence(DOMAINS);
    
    private static final String[] FRUITS = English.fruitsA2Z()
                                                .map(String::toLowerCase)
                                                .all().toArray(String.class);
    
    private static final FlSequence<String> SEQ_MORE_DOMAINS = Sequence.sequence(String.class, 26L*89989*3, n -> {
        return FRUITS[(int) (n % 26)] + (10000 + n % 89989) + "." + DOMAINS[(int) (n % 3)];
    });
    
    private static final FlSequence<String> SEQ_EMAILS = English.firstNames().repeat()
                .map(English.lastNames().repeat(), (f,l) -> f + "." + l)
                .map(domains().repeat(), (n,d) -> n + "@" + d);
}
