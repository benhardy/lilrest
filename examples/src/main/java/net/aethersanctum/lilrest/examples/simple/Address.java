/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.aethersanctum.lilrest.examples.simple;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Demo data class showing JDK 8 time and Optional classes.
 * <p>
 * Importantly, this is also an immutable object. There are no setters, so the only way Jackson
 * can set the fields is in the constructor. And because constructor parameter names are not present
 * in bytecode, we have to provide mappings from their JSON property names to their positions in the
 * constructor parameter list.
 * <p>
 * If we wanted to be less of sticklers about immutability then adding setters would allow us to use
 * more automatic means of mapping field names to JSON property names.
 */
public final class Address {
    private final LocalDateTime created;
    private final String streetAddress1;
    private final Optional<String> streetAddress2;
    private final Optional<String> suiteOrApartment;
    private final String city;
    private final Optional<String> county;
    private final Optional<String> postalCode;
    private final String country;

    /**
     * No way around property annotations here, constructor parameter names are
     * absent from bytecode.
     */
    @JsonCreator
    public Address(@JsonProperty(value = "created", required = true) LocalDateTime created,
                   @JsonProperty(value = "streetAddress1", required = true) String streetAddress1,
                   @JsonProperty("streetAddress2") Optional<String> streetAddress2,
                   @JsonProperty("suiteOrApartment") Optional<String> suiteOrApartment,
                   @JsonProperty(value = "city", required = true) String city,
                   @JsonProperty("county") Optional<String> county,
                   @JsonProperty("postalCode") Optional<String> postalCode,
                   @JsonProperty(value = "country", required = true) String country) {
        this.created = created;
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.suiteOrApartment = suiteOrApartment;
        this.city = city;
        this.county = county;
        this.postalCode = postalCode;
        this.country = country;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public Optional<String> getStreetAddress2() {
        return streetAddress2;
    }

    public Optional<String> getSuiteOrApartment() {
        return suiteOrApartment;
    }

    public String getCity() {
        return city;
    }

    public Optional<String> getCounty() {
        return county;
    }

    public Optional<String> getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Address address = (Address) o;

        return streetAddress1.equals(address.streetAddress1)
                && streetAddress2.equals(address.streetAddress2)
                && suiteOrApartment.equals(address.suiteOrApartment)
                && city.equals(address.city)
                && county.equals(address.county)
                && postalCode.equals(address.postalCode)
                && country.equals(address.country);
    }

    @Override
    public int hashCode() {
        int result = streetAddress1.hashCode();
        result = 31 * result + streetAddress2.hashCode();
        result = 31 * result + suiteOrApartment.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + county.hashCode();
        result = 31 * result + postalCode.hashCode();
        result = 31 * result + country.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("created", created)
                .append("streetAddress1", streetAddress1)
                .append("streetAddress2", streetAddress2.orElse(""))
                .append("suiteOrApartment", suiteOrApartment.orElse(""))
                .append("city", city)
                .append("county", county.orElse(""))
                .append("postalCode", postalCode.orElse(""))
                .append("country", country)
                .toString();
    }
}
