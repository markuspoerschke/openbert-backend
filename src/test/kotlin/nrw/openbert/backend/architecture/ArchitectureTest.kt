package nrw.openbert.backend.architecture

import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

class ArchitectureTest {
    @Test
    fun `dependencies between layers point inward`() {
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain")
            .definedBy("nrw.openbert.backend.*.domain..")
            .layer("Application")
            .definedBy("nrw.openbert.backend.*.application..")
            .layer("Adapters")
            .definedBy("nrw.openbert.backend.*.adapter..")
            .layer("Bootstrap")
            .definedBy("nrw.openbert.backend")
            .whereLayer("Domain")
            .mayOnlyBeAccessedByLayers("Application", "Adapters", "Bootstrap")
            .whereLayer("Application")
            .mayOnlyBeAccessedByLayers("Adapters", "Bootstrap")
            .whereLayer("Adapters")
            .mayOnlyBeAccessedByLayers("Bootstrap")
            .whereLayer("Bootstrap")
            .mayNotBeAccessedByAnyLayer()
            .ensureAllClassesAreContainedInArchitecture()
            .check(productionClasses)
    }

    @Test
    fun `ports depend only on domain models and other port contracts within the backend`() {
        classes()
            .that()
            .resideInAPackage("..application.port..")
            .should()
            .onlyDependOnClassesThat(
                not(resideInAPackage("nrw.openbert.backend.."))
                    .or(resideInAnyPackage("..domain..", "..application.port..")),
            ).because("port contracts must not depend on use case implementations or adapters")
            .check(productionClasses)
    }

    @Test
    fun `inbound and outbound ports are interfaces`() {
        classes()
            .that()
            .resideInAnyPackage("..application.port.inbound..", "..application.port.outbound..")
            .should()
            .beInterfaces()
            .check(productionClasses)
    }

    companion object {
        private val productionClasses =
            ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("nrw.openbert.backend")
    }
}
