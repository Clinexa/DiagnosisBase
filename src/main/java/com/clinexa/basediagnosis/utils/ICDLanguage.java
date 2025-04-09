/*
 * This file is part of Clinexa DiagnosisBase.
 *
 * Clinexa DiagnosisBase is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Clinexa DiagnosisBase is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License and GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License and GNU General Public
 * License along with Clinexa DiagnosisBase. If not, see <https://www.gnu.org/licenses/>.
 */

package com.clinexa.basediagnosis.utils;

/**
 * Languages supported by the ICD 11 system.
 *
 * @since 0.1-dev.1
 * @author Nikita S.
 */
public enum ICDLanguage {
    ARABIC("ar"),
    CHINESE("zh"),
    CZECH("cs"),
    ENGLISH("en"),
    FRENCH("fr"),
    KAZAKH("kk"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    SLOVAK("sk"),
    SPANISH("es"),
    SWEDISH("sv"),
    TURKISH("tr"),
    UZBEK("uz");

    private final String code;

    ICDLanguage(String code) {
        this.code = code;
    }

    /**
     * Returns String description of the object.
     *
     * @return String description of the object
     */
    @Override
    public String toString() {
        return "ICD11Language{" +
                "code='" + code + '\'' +
                '}';
    }

    /**
     * Returns ISO 639 codes for the language.
     *
     * @return code of the language.
     * @see <a href="https://www.iso.org/iso-639-language-code">ISO 639</a>
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets an enum value for the given ISO 638 code.
     *
     * @param code code to search for.
     * @return ICDLanguage enum value for the given code.
     * @throws EnumConstantNotPresentException if no value is associated with the code.
     */
    @SuppressWarnings("unused")
    public static ICDLanguage getByCode(String code) {
        for (ICDLanguage language : ICDLanguage.values()) {
            if (code.equalsIgnoreCase(language.getCode()))
                return language;
        }
        throw new EnumConstantNotPresentException(ICDLanguage.class, code);
    }
}
