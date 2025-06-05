package com.netharus.controller.utilityForControllers;

import com.netharus.lock.EmergencyStop;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PageBuilder {

    private final EmergencyStop emergencyStop;

    public PageBuilderImpl builder() {
        return new PageBuilderImpl();
    }

    public class PageBuilderImpl {
        private final Map<String, Object> model = new HashMap<>();

        public PageBuilderImpl username(String username) {
            model.put("username", username);
            return this;
        }

        public PageBuilderImpl pageTitle(String pageTitle) {
            model.put("pageTitle", pageTitle);
            return this;
        }

        public PageBuilderImpl fragment(String fragment) {
            model.put("fragment", fragment);
            return this;
        }

        public PageBuilderImpl pageContainer(Object pageContainer) {
            model.put("pageContainer", pageContainer);
            return this;
        }

        public PageBuilderImpl gestures(Object gestures) {
            model.put("gestures", gestures);
            return this;
        }

        public PageBuilderImpl events(Object events) {
            model.put("events", events);
            return this;
        }

        public ModelAndView build() {
            model.put("emergencyStopActive", emergencyStop.isActive());
            String viewName = "homePage";
            ModelAndView mav = new ModelAndView(viewName);
            mav.addAllObjects(model);
            return mav;
        }
    }
}

