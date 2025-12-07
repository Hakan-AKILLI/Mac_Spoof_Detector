import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MacSpoofDetector {

    // Önceki kayıtları tutar
    private static final Map<String, String> arpTable = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("MAC Klonlama Tespit Sistemi Başlatıldı\n");

        while (true) {
            try {
                checkForSpoofing();
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    // ARP çıktısını okur
    private static void checkForSpoofing() throws Exception {
        Process process;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            process = Runtime.getRuntime().exec("arp -a");
        } else {
            process = Runtime.getRuntime().exec("arp -n");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            parseArpLine(line);
        }
    }
    public static String  findFirstDuplicateValue() {
        Set<String> seen = new HashSet<>();
        for (String value : arpTable.values()) {
            if (!seen.add(value)) {
                return value; // duplicate bulundu
            }
        }
        return null;
    }


    // ARP satırını çözümle
    private static void parseArpLine(String line) {
        String cleaned = line.trim();

        // MAC format kontrolü
        if (!cleaned.matches(".*([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}.*")) return;

        String[] parts = cleaned.split("\\s+");

        if (parts.length < 2) return;

        String ip = parts[0];
        String mac = parts[1];

        // Yeni kayıt
        if (!arpTable.containsKey(ip)) {
            arpTable.put(ip, mac);
            return;
        }

        String oldMac = arpTable.get(ip);
        String duplicateValue = findFirstDuplicateValue();
        // MAC değiştiyse ALARM ver
        if (!oldMac.equals(mac)) {
            System.out.println(" SPOOFING TESPİT EDİLDİ!");
            System.out.println("IP: " + ip);
            System.out.println("Eski MAC: " + oldMac);
            System.out.println("Yeni MAC: " + mac);
            System.out.println("-----------------------------");
        }
    }
}
