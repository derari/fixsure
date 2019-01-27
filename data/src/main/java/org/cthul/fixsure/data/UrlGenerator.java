package org.cthul.fixsure.data;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.LongFunction;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Fixsure;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlDataSource;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.AnonymousGenerator;
import org.cthul.fixsure.generators.AnonymousTemplate;

/**
 *
 */
public class UrlGenerator extends AnonymousGenerator<String> {
    
    private static final Template TEMPLATE = new Template();
    
    public static Template urls() {
        return TEMPLATE;
    }
    
    private final Generator<String> protocols;
    private final Generator<Boolean> hasProtocol;
    private final Generator<String> hosts;
    private final Generator<Boolean> hasAuthority;
    private final Generator<Boolean> isAbsolute;
    private final Generator<Integer> ports;
    private final Generator<Boolean> hasPort;
    private final Generator<String> paths;
    private final Generator<Boolean> hasPath;

    public UrlGenerator(
            DataSource<String> protocols, DataSource<Boolean> hasProtocol, 
            DataSource<String> hosts, DataSource<Boolean> hasAuthority, 
            DataSource<Boolean> isAbsolute, 
            DataSource<Integer> ports, DataSource<Boolean> hasPort, 
            DataSource<String> paths, DataSource<Boolean> hasPath) {
        if (hasProtocol != null) {
            Objects.requireNonNull(protocols, "protocol generator");
        }
        if (hasAuthority != null) {
            Objects.requireNonNull(hosts, "host generator");
        }
        Objects.requireNonNull(isAbsolute, "absolute toggle");
        if (hasPort != null) {
            Objects.requireNonNull(ports, "port generator");
        }
        if (hasPath != null) {
            Objects.requireNonNull(paths);
        }
        this.protocols = toGenerator(protocols);
        this.hasProtocol = toGenerator(hasProtocol);
        this.hosts = toGenerator(hosts);
        this.hasAuthority = toGenerator(hasAuthority);
        this.isAbsolute = toGenerator(isAbsolute);
        this.ports = toGenerator(ports);
        this.hasPort = toGenerator(hasPort);
        this.paths = toGenerator(paths);
        this.hasPath = toGenerator(hasPath);
    }
    
    private static <T> Generator<T> toGenerator(DataSource<T> dataSource) {
        return dataSource == null ? null : dataSource.toGenerator();
    }
    
    private boolean nextHasAuthority() {
        return hosts != null && (hasAuthority == null || hasAuthority.next());
    }
    
    private boolean nextIsAbsolute() {
        return isAbsolute.next();
    }
    
    private boolean nextHasProtocol() {
        return protocols != null && (hasProtocol == null || hasProtocol.next());
    }
    
    private boolean nextHasPort() {
        return ports != null && (hasPort == null || hasPort.next());
    }
    
    private boolean nextHasPath() {
        return paths != null && (hasPath == null || hasPath.next());
    }
    
    @Override
    public String next() {
        StringBuilder sb = new StringBuilder();
        if (nextIsAbsolute()) {
            if (nextHasAuthority()) {
                if (nextHasProtocol()) {
                    sb.append(protocols.next()).append(":");
                }
                sb.append("//").append(hosts.next());
                if (nextHasPort()) {
                    sb.append(":").append(ports.next());
                }
            }
            sb.append("/");
        }
        if (nextHasPath()) {
            sb.append(paths.next());
        }
        return sb.toString();
    }

    
    public static class Template extends AnonymousTemplate<String> {
        
        private static final DataSource<Boolean> ALWAYS_FALSE = Fixsure.constant(false);
        private static final DataSource<Boolean> ALWAYS_TRUE = Fixsure.constant(true);
        private static final LongFunction<DataSource<Boolean>> OFF = seed -> ALWAYS_FALSE;
        private static final LongFunction<DataSource<Boolean>> ON = seed -> ALWAYS_TRUE;
        private static final FlSequence<String> DEFAULT_PROTOCOLS_SRC = Fixsure.sequence("http", "https");
        private static final LongFunction<DataSource<String>> DEFAULT_PROTOCOLS = DEFAULT_PROTOCOLS_SRC::random;
        private static final LongFunction<DataSource<String>> DEFAULT_HOSTS = Web.domains()::random;
        private static final LongFunction<DataSource<Integer>> DEFAULT_PORTS = Fixsure.integers(1024, 1<<16)::random;
        private static final FlSequence<String> DEFAULT_SEGMENTS_SRC = 
                Fixsure.integers(0, 1<<16).ordered().map(Object::toString).alternateWith(
                    English.vegetablesA2Z().repeat().map(String::toLowerCase),
                    Fixsure.integers(0, 1<<16).ordered().map(Integer::toHexString)
                );
        private static final FlSequence<String> DEFAULT_SEGMENT_EXT_SRC = Fixsure.sequence("", "/", ".html", ".html", ".png", ".mp4");
        private static final LongFunction<DataSource<String>> DEFAULT_SEGMENTS = DEFAULT_SEGMENTS_SRC::random;
        private static final LongFunction<DataSource<String>> DEFAULT_SEGMENT_EXT = DEFAULT_SEGMENT_EXT_SRC::random;
        private static final LongFunction<DataSource<Integer>> DEFAULT_PATH_LENGTH = Fixsure.integers(1, 5)::random;
        private static final long DEFAULT_SEED = DistributionRandomizer.toSeed(UrlGenerator.class);
        
        private static final long SEED_PROTOCOLS = DistributionRandomizer.toSeed("URL", "protocols");
        private static final long SEED_HOSTS = DistributionRandomizer.toSeed("URL", "hosts");
        private static final long SEED_PORTS = DistributionRandomizer.toSeed("URL", "ports");
        private static final long SEED_PATHS = DistributionRandomizer.toSeed("URL", "paths");
        private static final long SEED_SEGMENTS = DistributionRandomizer.toSeed("URL", "segments");
        private static final long SEED_SEGMENT_EXTS = DistributionRandomizer.toSeed("URL", "segmentExts");
        private static final long SEED_TOGGLE = DistributionRandomizer.toSeed("URL", "toggle");
        
        private final LongFunction<DataSource<String>> protocols;
        private final LongFunction<DataSource<Boolean>> hasProtocol;
        private final LongFunction<DataSource<String>> hosts;
        private final LongFunction<DataSource<Boolean>> hasAuthority;
        private final LongFunction<DataSource<Boolean>> isAbsolute;
        private final LongFunction<DataSource<Integer>> ports;
        private final LongFunction<DataSource<Boolean>> hasPort;
        private final LongFunction<DataSource<String>> paths;
        private final LongFunction<DataSource<String>> segments;
        private final LongFunction<DataSource<String>> segmentExtensions;
        private final LongFunction<DataSource<Integer>> segmentCount;
        private final LongFunction<DataSource<Boolean>> hasPath;
        private final long seed;

        public Template() {
            this.protocols = DEFAULT_PROTOCOLS;
            this.hasProtocol = ON;
            this.hosts = DEFAULT_HOSTS;
            this.hasAuthority = ON;
            this.isAbsolute = ON;
            this.ports = DEFAULT_PORTS;
            this.hasPort = ON;
            this.paths = null;
            this.segments = DEFAULT_SEGMENTS;
            this.segmentExtensions = DEFAULT_SEGMENT_EXT;
            this.segmentCount = DEFAULT_PATH_LENGTH;
            this.hasPath = ON;
            this.seed = DEFAULT_SEED;
        }

        public Template(LongFunction<DataSource<String>> protocols, LongFunction<DataSource<Boolean>> hasProtocol, LongFunction<DataSource<String>> hosts, LongFunction<DataSource<Boolean>> hasAuthority, LongFunction<DataSource<Boolean>> isAbsolute, LongFunction<DataSource<Integer>> ports, LongFunction<DataSource<Boolean>> hasPort, LongFunction<DataSource<String>> paths, LongFunction<DataSource<String>> segments, LongFunction<DataSource<String>> segmentExtensions, LongFunction<DataSource<Integer>> segmentCount, LongFunction<DataSource<Boolean>> hasPath, long seed) {
            this.protocols = protocols;
            this.hasProtocol = hasProtocol;
            this.hosts = hosts;
            this.hasAuthority = hasAuthority;
            this.isAbsolute = isAbsolute;
            this.ports = ports;
            this.hasPort = hasPort;
            this.paths = paths;
            this.segments = segments;
            this.segmentExtensions = segmentExtensions;
            this.segmentCount = segmentCount;
            this.hasPath = hasPath;
            this.seed = seed;
        }

        public Template protocols(LongFunction<DataSource<String>> protocols) {
            LongFunction<DataSource<Boolean>> newHasProtocol = this.hasProtocol;
            if (newHasProtocol == OFF) newHasProtocol = ON;
            return new Template(protocols, newHasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template protocols(DataSource<String> protocols) {
            return protocols(s -> protocols);
        }
        
        public Template protocols(String... protocols) {
            return protocols(s -> Fixsure.sequence(protocols).random(s));
        }
        
        public Template protocol(String protocol) {
            return protocols(s -> Fixsure.constant(protocol));
        }
        
        public Template hasProtocol(LongFunction<DataSource<Boolean>> hasProtocol) {
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template hasProtocol(DataSource<Boolean> hasProtocol) {
            return hasProtocol(s -> hasProtocol);
        }
        
        public Template hasProtocol(double protocolRatio) {
            return hasProtocol(ratioToToggle(protocolRatio));
        }
        
        public Template hasProtocol(boolean hasProtocol) {
            return hasProtocol(hasProtocol ? ON : OFF);
        }
        
        public Template hosts(LongFunction<DataSource<String>> hosts) {
            LongFunction<DataSource<Boolean>> newIsAbsolute = this.isAbsolute;
            if (newIsAbsolute == OFF) newIsAbsolute = ON;
            LongFunction<DataSource<Boolean>> newHasAuthority = this.hasAuthority;
            if (newHasAuthority == OFF) newHasAuthority = ON;
            return new Template(protocols, hasProtocol, hosts, newHasAuthority, newIsAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template hosts(DataSource<String> hosts) {
            return hosts(s -> hosts);
        }
        
        public Template hosts(String... hosts) {
            return hosts(s -> Fixsure.sequence(hosts).random(s));
        }
        
        public Template host(String host) {
            return hosts(s -> Fixsure.constant(host));
        }
        
        public Template hasAuthority(LongFunction<DataSource<Boolean>> hasAuthority) {
            LongFunction<DataSource<Boolean>> newIsAbsolute = this.isAbsolute;
            if (newIsAbsolute == OFF) newIsAbsolute = ON;
            return new Template(protocols, hasProtocol, hosts, hasAuthority, newIsAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template hasAuthority(DataSource<Boolean> hasAuthority) {
            return hasAuthority(s -> hasAuthority);
        }
        
        public Template hasAuthority(double hostRatio) {
            return hasAuthority(ratioToToggle(hostRatio));
        }
        
        public Template hasAuthority(boolean hasAuthority) {
            return hasAuthority(hasAuthority ? ON : OFF);
        }
        
        public Template isAbsolute(LongFunction<DataSource<Boolean>> isAbsolute) {
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template isAbsolute(DataSource<Boolean> isAbsolute) {
            return isAbsolute(s -> isAbsolute);
        }
        
        public Template isAbsolute(double hostRatio) {
            return isAbsolute(ratioToToggle(hostRatio));
        }
        
        public Template isAbsolute(boolean isAbsolute) {
            return isAbsolute(isAbsolute ? ON : OFF);
        }
        
        public Template ports(LongFunction<DataSource<Integer>> ports) {
            LongFunction<DataSource<Boolean>> newHasPort = this.hasPort;
            if (newHasPort == OFF) newHasPort = ON;
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, newHasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template ports(DataSource<Integer> ports) {
            return ports(s -> ports);
        }
        
        public Template ports(Integer... ports) {
            return ports(s -> Fixsure.sequence(ports).random(s));
        }
        
        public Template port(int port) {
            return ports(s -> Fixsure.constant(port));
        }
        
        public Template hasPort(LongFunction<DataSource<Boolean>> hasPort) {
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template hasPort(DataSource<Boolean> hasPort) {
            return hasPort(s -> hasPort);
        }
        
        public Template hasPort(double portRatio) {
            return hasPort(ratioToToggle(portRatio));
        }
        
        public Template hasPort(boolean hasPort) {
            return hasPort(hasPort ? ON : OFF);
        }

        public Template paths(LongFunction<DataSource<String>> paths) {
            LongFunction<DataSource<Boolean>> newHasPath = this.hasPath;
            if (newHasPath == OFF) newHasPath = ON;
            return new Template(protocols, hasPath, hosts, hasAuthority, isAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, newHasPath, seed);
        }
        
        public Template paths(DataSource<String> paths) {
            return paths(s -> paths);
        }
        
        public Template paths(String... paths) {
            return paths(s -> Fixsure.sequence(paths).random(s));
        }
        
        public Template path(String path) {
            return paths(s -> Fixsure.constant(path));
        }
        
        public Template hasPath(LongFunction<DataSource<Boolean>> hasPath) {
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template hasPath(DataSource<Boolean> hasPath) {
            return hasPath(s -> hasPath);
        }
        
        public Template hasPath(double pathRatio) {
            return hasPath(ratioToToggle(pathRatio));
        }
        
        public Template hasPath(boolean hasPath) {
            return hasPath(hasPath ? ON : OFF);
        }

        public Template segments(LongFunction<DataSource<String>> segments) {
            LongFunction<DataSource<Boolean>> newHasPath = this.hasPath;
            if (newHasPath == OFF) newHasPath = ON;
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, null, segments, segmentExtensions, segmentCount, newHasPath, seed);
        }
        
        public Template segments(DataSource<String> segments) {
            return segments(s -> segments);
        }
        
        public Template segments(String... segments) {
            return segments(s -> Fixsure.sequence(segments).random(s));
        }

        public Template segmentCount(LongFunction<DataSource<Integer>> segmentCount) {
            LongFunction<DataSource<Boolean>> newHasPath = this.hasPath;
            if (newHasPath == OFF) newHasPath = ON;
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, null, segments, segmentExtensions, segmentCount, newHasPath, seed);
        }
        
        public Template segmentCount(DataSource<Integer> segmentCount) {
            return segmentCount(s -> segmentCount);
        }
        
        public Template segmentCount(Integer... segmentCount) {
            return segmentCount(s -> Fixsure.sequence(segmentCount).random(s));
        }
        
        public Template segmentCount(int segmentCount) {
            return segmentCount(s -> Fixsure.constant(segmentCount));
        }
        
        public Template segmentExts(LongFunction<DataSource<String>> segmentExts) {
            LongFunction<DataSource<Boolean>> newHasPath = this.hasPath;
            if (newHasPath == OFF) newHasPath = ON;
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, null, segments, segmentExts, segmentCount, newHasPath, seed);
        }
        
        public Template segmentExts(DataSource<String> segmentExts) {
            return segmentExts(s -> segmentExts);
        }
        
        public Template segmentExts(String... segmentExts) {
            return segmentExts(s -> Fixsure.sequence(segmentExts).random(s));
        }
        
        public Template seed(long seed) {
            return new Template(protocols, hasProtocol, hosts, hasAuthority, isAbsolute, ports, hasPort, paths, segments, segmentExtensions, segmentCount, hasPath, seed);
        }
        
        public Template segmentExt(String segmentExt) {
            return segmentExts(s -> Fixsure.constant(segmentExt));
        }
        
        private LongFunction<DataSource<Boolean>> ratioToToggle(double ratio) {
            if (ratio <= 0) return OFF;
            if (ratio >= 1) return ON;
            return s -> Fixsure.booleans(ratio, Fixsure.uniform(), s);
        }

        @Override
        public FlGenerator<String> newGenerator() {
            final DataSource<String> protocolsSrc, hostsSrc, pathsSrc;
            final DataSource<Integer> portsSrc;
            final DataSource<Boolean> hasProtocolSrc, hasAuthoritySrc, isAbsoluteSrc, hasPortSrc, hasPathSrc;
            
            isAbsoluteSrc = isAbsolute.apply(seed ^ SEED_TOGGLE);
            if (hasAuthority == OFF || isAbsolute == OFF) {
                protocolsSrc = null;
                hasProtocolSrc = null;
                hostsSrc = null;
                hasAuthoritySrc = null;
                portsSrc = null;
                hasPortSrc = null;
            } else {
                if (hasProtocol == OFF) {
                    protocolsSrc = null;
                    hasProtocolSrc = null;
                } else {
                    protocolsSrc = protocols.apply(seed ^ SEED_PROTOCOLS);
                    hasProtocolSrc = newToggleSrc(hasProtocol, SEED_PROTOCOLS);
                }
                hostsSrc = hosts.apply(seed ^ SEED_HOSTS);
                hasAuthoritySrc = newToggleSrc(hasAuthority, SEED_HOSTS);
                if (hasPort == OFF) {
                    portsSrc = null;
                    hasPortSrc = null;
                } else {
                    portsSrc = ports.apply(seed ^ SEED_PORTS);
                    hasPortSrc = newToggleSrc(hasPort, SEED_PORTS);
                }
            }
            if (hasPath == OFF) {
                pathsSrc = null;
                hasPathSrc = null;
            } else {
                if (paths != null) {
                    pathsSrc = paths.apply(seed ^ SEED_PATHS);
                } else {
                    pathsSrc = buildPathsSrc();
                }
                hasPathSrc = newToggleSrc(hasPath, SEED_PATHS);
            }
            return new UrlGenerator(protocolsSrc, hasProtocolSrc, hostsSrc, hasAuthoritySrc, isAbsoluteSrc, portsSrc, hasPortSrc, pathsSrc, hasPathSrc);
        }

        private DataSource<Boolean> newToggleSrc(LongFunction<DataSource<Boolean>> toggle, long seedFlag) {
            if (toggle == ON) return null;
            return toggle.apply(seed ^ seedFlag ^ SEED_TOGGLE);
        }
        
        private FlDataSource<String> buildPathsSrc() {
            FlDataSource<String> segmentSrc = segments.apply(seed ^ SEED_SEGMENTS).fluentData();
            DataSource<Integer> lengthSrc = segmentCount.apply(seed ^ SEED_SEGMENTS ^ SEED_TOGGLE);
            DataSource<String> extSrc = segmentExtensions.apply(seed ^ SEED_SEGMENT_EXTS);
            return segmentSrc.aggregate(lengthSrc)
                    .map(v -> String.join("/", v))
                    .map(extSrc, (p, e) -> p + e);
        }
        
        public FlTemplate<URL> toURLs() {
            return toURLs(null);
        }
        
        public FlTemplate<URL> toURLs(URL context) {
            return map(url -> {
                try {
                    return new URL(context, url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(url, e);
                }
            });
        }
        
        public FlTemplate<URI> toURIs() {
            return map(url -> {
                try {
                    return new URI(url);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(url, ex);
                }
            });
        }
        
        public FlTemplate<Path> toPaths() {
            return map(url -> {
                try {
                    return Paths.get(new URI(url));
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(url, ex);
                }
            });
        }
    }
}
