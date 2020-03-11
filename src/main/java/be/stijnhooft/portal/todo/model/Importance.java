package be.stijnhooft.portal.todo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Importance {

    I_DO_NOT_REALLY_CARE,
    NOT_SO_IMPORTANT,
    IMPORTANT,
    VERY_IMPORTANT;

}
