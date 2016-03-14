package com.magnet.magnetchat.ui.views.section.login;

import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by dlernatovich on 2/29/16.
 */
public class LoginViewProperties extends ViewProperty {

    private LoginViewProperties() {

    }

    public static class PropertyBuilder extends AbstractPropertyBuilder<LoginViewProperties> {

        public PropertyBuilder() {
            super();
            typeface = null;
        }

        public PropertyBuilder setLogoStyle(int logoStyle) {
            return this;
        }

        @Override
        public LoginViewProperties build() {
            LoginViewProperties properties = new LoginViewProperties();
            properties.typeface = typeface;
            return properties;
        }
    }

}
