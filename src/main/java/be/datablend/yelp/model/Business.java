package be.datablend.yelp.model;

import java.util.*;

/**
 * User: dsuvee (datablend.be)
 * Date: 28/11/13
 */
public class Business {

    private String id;
    private String name;
    private String city;
    private Set<String> categories;
    private Map<String,Long> checkIns;

    private Business() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public Map<String, Long> getCheckIns() {
        return checkIns;
    }

    public long getNumberOfCheckIns() {
        long numberOfCheckIns = 0;
        for (Long value : checkIns.values()) {
            numberOfCheckIns = numberOfCheckIns + value;
        }
        return numberOfCheckIns;
    }

    public Long getCheckIn(String checkInKey) {
        return checkIns.get(checkInKey);
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Business business = (Business) o;
        if (id != null ? !id.equals(business.id) : business.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static class Builder {
        private String id;
        private String name;
        private String city;
        private Set<String> categories = new HashSet<String>();
        private Map<String,Long> checkIns = new LinkedHashMap<String, java.lang.Long>();

        private Builder() {
            // Pre-initialize checkins
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 7; j++) {
                    checkIns.put(i + "-" + j, 0L);
                }
            }
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder addCategory(String category) {
            this.categories.add(category);
            return this;
        }

        public Builder addCheckIn(String checkInKey, long numberOfCheckIns) {
            this.checkIns.put(checkInKey, numberOfCheckIns);
            return this;
        }

        public Business build() {
            final Business business = new Business();
            business.id = id;
            business.name = name;
            business.city = city;
            business.categories = categories;
            business.checkIns = checkIns;
            return business;
        }

    }

}
