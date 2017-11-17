package org.cthul.fixsure.data;

import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.generators.value.ItemsSequence;

/**
 *
 */
public class Web {
    
    public static FlSequence<String> domains() {
        return SEQ_DOMAINS;
    }
    
    public static FlSequence<String> moreDomains() {
        return SEQ_MORE_DOMAINS;
    }
    
    public static FlSequence<String> emailAddresses() {
        return SEQ_EMAILS;
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
