package romelo333.notenoughwands;

// @todo fabric: Temporary class to replace configuration
public class Configuration {

    public Property get(String category, String name, double def, String description) {
        return new Property(def);
    }
    public Property get(String category, String name, double def) {
        return new Property(def);
    }
    public Property get(String category, String name, int def, String description) {
        return new Property(def);
    }
    public Property get(String category, String name, boolean def, String description) {
        return new Property(def);
    }
    public boolean getBoolean(String category, String name, boolean def, String description) {
        return def;
    }
    public String getString(String category, String name, String def, String description) {
        return def;
    }



    public static class Property {
        private final Object object;

        public Property(Object object) {
            this.object = object;
        }

        public double getDouble() {
            return (Double) object;
        }

        public int getInt() {
            return (Integer) object;
        }

        public boolean getBoolean() {
            return (Boolean) object;
        }
    }
}