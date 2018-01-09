package dagger.internal.codegen;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

@AutoValue
public abstract class SymEncPara {
    static Builder builder(){
        return new AutoValue_SymEncPara.Builder();
    }

    abstract TypeElement typeElement();

    abstract String className();

    abstract String algorithm();

    abstract String blockMode();

    abstract String paddingMode();

    abstract int keySize();

    abstract Optional<Name> keyMethodName();

    abstract Optional<Name> ivParameterMethodName();

    @AutoValue.Builder
    abstract static class Builder{
        abstract Builder setTypeElement(TypeElement typeElement);

        abstract Builder setClassName(String className);

        abstract Builder setAlgorithm(String algorithm);

        abstract Builder setBlockMode(String blockMode);

        abstract Builder setPaddingMode(String paddingMode);

        abstract Builder setKeySize(int keySize);

        abstract Builder setKeyMethodName(Optional<Name> keyMethodName);

        abstract Builder setIvParameterMethodName(Optional<Name> ivParameterMethodName);

        abstract SymEncPara build();
    }
}
