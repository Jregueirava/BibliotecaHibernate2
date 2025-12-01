# Decisiones sobre Nomenclatura y Relaciones - Proyecto BibliotecaHibernate

## Situación actual

Has identificado diferencias entre tu código actual y el código del profesor en la solución del ejercicio de Muchos a Muchos. Este documento te ayudará a decidir qué camino seguir.

---

## DIFERENCIA 1: Nomenclatura de atributos Java

### Tu código actual:
```java
@Column(name = "fecha_nacimiento")
private LocalDate fecha_nacimiento;  // ← snake_case en Java
```

### Código del profesor:
```java
@Column(name = "fecha_nacimiento")
private LocalDate fechaNacimiento;  // ← camelCase en Java
```

### Convención estándar de Java (Oracle):
- **Columnas de BD**: snake_case → `fecha_nacimiento`
- **Atributos Java**: camelCase → `fechaNacimiento`
- **Clases**: PascalCase → `Usuario`, `Libro`
- **Métodos**: camelCase → `getFechaNacimiento()`
- **Constantes**: UPPER_SNAKE_CASE → `MAX_INTENTOS`

### Análisis:

| Aspecto | Tu enfoque (snake_case) | Enfoque del profesor (camelCase) |
|---------|------------------------|----------------------------------|
| **Convención Java** | ❌ No sigue la convención | ✅ Sigue la convención oficial |
| **Legibilidad** | 🟡 Consistente con BD | ✅ Estándar en proyectos Java |
| **Profesional** | ❌ Poco común en industria | ✅ Esperado en código profesional |
| **Compatibilidad** | ✅ Funciona perfectamente | ✅ Funciona perfectamente |
| **Evaluación profesor** | ❓ Podría restar puntos | ✅ Esperado por el profesor |

### Impacto del cambio:

Si decides cambiar a camelCase, afectaría a:
- ✏️ `Usuario.java`: fecha_nacimiento → fechaNacimiento, fecha_registro → fechaRegistro
- ✏️ `Libro.java`: fecha_publicacion → fechaPublicacion, autor_id → autorId, categoria_id → categoriaId
- ✏️ Todos los getters/setters correspondientes
- ✏️ Cualquier código en App.java que use estos atributos

**Tiempo estimado:** 15-20 minutos de refactorización

---

## DIFERENCIA 2: Relación Usuario ↔ Prestamo

### Tu código actual:
```java
// En Usuario.java
@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
private Prestamo prestamo;  // UN usuario → UN préstamo
```

```java
// En Prestamo.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

**Significado:** Un usuario solo puede tener UN préstamo (activo o histórico)

### Código del profesor:
```java
// En Usuario.java
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Prestamo> prestamos;  // UN usuario → MUCHOS préstamos
```

```java
// En Prestamo.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

**Significado:** Un usuario puede tener VARIOS préstamos (múltiples a lo largo del tiempo)

### Análisis de cuál tiene más sentido:

| Aspecto | Tu enfoque (@OneToOne) | Enfoque del profesor (@OneToMany) |
|---------|------------------------|-----------------------------------|
| **Realismo** | ❌ Muy limitado | ✅ Refleja realidad de biblioteca |
| **Escalabilidad** | ❌ No permite histórico | ✅ Permite múltiples préstamos |
| **Datos actuales** | ✅ Funciona con BD actual | ⚠️ Requiere ajuste conceptual |
| **Lógica de negocio** | ❌ Usuario solo 1 préstamo total | ✅ Usuario puede tener varios |
| **Solución profesor** | ❌ No coincide | ✅ Coincide exactamente |

### ¿Qué permite cada enfoque?

#### Con @OneToOne (tu código actual):
- ✅ Un usuario tiene un préstamo
- ❌ No puede tener histórico de préstamos
- ❌ No puede tener múltiples préstamos simultáneos
- ❌ Si devuelve un libro, pierde el registro del préstamo anterior

#### Con @OneToMany (código profesor):
- ✅ Un usuario puede tener múltiples préstamos
- ✅ Puede tener histórico (préstamos devueltos)
- ✅ Puede tener varios préstamos activos
- ✅ Lógica más realista de biblioteca

### Impacto del cambio:

Si decides cambiar a @OneToMany:

**En Usuario.java:**
```java
// Cambiar esto:
@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
private Prestamo prestamo;

public Prestamo getPrestamo() { return prestamo; }
public void setPrestamo(Prestamo prestamo) { this.prestamo = prestamo; }

// Por esto:
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Prestamo> prestamos;

public List<Prestamo> getPrestamos() { return prestamos; }
public void setPrestamos(List<Prestamo> prestamos) { this.prestamos = prestamos; }

// Añadir métodos helper:
public void addPrestamo(Prestamo prestamo) {
    prestamos.add(prestamo);
    prestamo.setUsuario(this);
}

public void removePrestamo(Prestamo prestamo) {
    prestamos.remove(prestamo);
    prestamo.setUsuario(null);
}

// Inicializar en constructores:
public Usuario() {
    prestamos = new ArrayList<>();
}
```

**En Prestamo.java:**
```java
// Cambiar esto:
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;

// Por esto:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

**En Ejemplar.java:**
- También tendrías que cambiar la relación Ejemplar ↔ Prestamo de @OneToOne a @OneToMany
- Porque un ejemplar puede tener múltiples préstamos a lo largo del tiempo

**Tiempo estimado:** 30-40 minutos de refactorización + pruebas

---

## DIFERENCIA 3: Relación Libro con Autor y Categoria

### Tu código actual:
```java
@Column (name = "autor_id")
private int autor_id;

@Column (name = "categoria_id")
private int categoria_id;
```
**Tipo:** Relación manual con IDs (no usa relaciones JPA)

### Código del profesor:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "autor_id")
private Autor autor;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;
```
**Tipo:** Relación @ManyToOne (muchos libros → un autor)

### Análisis:

| Aspecto | Tu enfoque (IDs manuales) | Enfoque del profesor (@ManyToOne) |
|---------|---------------------------|-----------------------------------|
| **Orientado a objetos** | ❌ Enfoque relacional puro | ✅ Enfoque OOP |
| **Navegabilidad** | ❌ Necesitas joins manuales | ✅ Automático con JPA |
| **Lazy Loading** | ❌ No disponible | ✅ Carga bajo demanda |
| **Facilidad de uso** | ❌ Más código manual | ✅ Más simple |
| **Para ejercicio M:M** | 🟡 No afecta funcionalidad | 🟡 No afecta funcionalidad |

### Impacto en el ejercicio de Muchos a Muchos:

⚠️ **IMPORTANTE:** Esta diferencia **NO afecta** el ejercicio de Muchos a Muchos (Usuario ↔ Libro favoritos).

Puedes hacer el ejercicio con IDs manuales o con relaciones @ManyToOne. Ambos funcionan.

---

## OPCIONES DISPONIBLES

### 📋 OPCIÓN A: Mantener tu código actual (cambio mínimo)

**Qué hacer:**
- ✅ Mantener snake_case en atributos Java (fecha_nacimiento)
- ✅ Mantener @OneToOne entre Usuario y Prestamo
- ✅ Mantener IDs manuales en Libro (autor_id, categoria_id)
- ✅ Solo añadir la relación @ManyToMany para favoritos

**Ventajas:**
- ⏱️ Rápido (10 minutos)
- 🔧 Cambios mínimos
- ✅ Funciona perfectamente

**Desventajas:**
- ❌ No sigue convenciones Java estándar
- ❌ No coincide con solución del profesor
- ⚠️ Podría restar puntos en la evaluación
- ❌ Modelo de datos limitado (solo 1 préstamo por usuario)

**Cuándo elegir esta opción:**
- Si solo quieres practicar y entender @ManyToMany
- Si no vas a entregar esto al profesor
- Si el tiempo es limitado

---

### 📋 OPCIÓN B: Seguir solución del profesor (cambio completo)

**Qué hacer:**
- 🔄 Cambiar snake_case a camelCase en todos los atributos Java
- 🔄 Cambiar @OneToOne a @OneToMany en Usuario ↔ Prestamo
- 🔄 Cambiar IDs manuales a relaciones @ManyToOne en Libro
- ✅ Añadir la relación @ManyToMany para favoritos

**Ventajas:**
- ✅ Sigue convenciones Java estándar
- ✅ Coincide 100% con solución del profesor
- ✅ Código más profesional y mantenible
- ✅ Modelo de datos más realista
- ✅ Mejor evaluación esperada

**Desventajas:**
- ⏱️ Requiere más tiempo (1-2 horas)
- 🔧 Muchos cambios en el código
- 🧪 Requiere pruebas exhaustivas

**Cuándo elegir esta opción:**
- ✅ Si vas a entregar esto al profesor
- ✅ Si quieres seguir buenas prácticas
- ✅ Si tienes tiempo para refactorizar
- ✅ Si quieres aprender bien las relaciones JPA

---

### 📋 OPCIÓN C: Solución intermedia (cambio parcial)

**Qué hacer:**
- 🔄 Cambiar snake_case a camelCase en atributos Java
- ✅ Mantener @OneToOne entre Usuario y Prestamo
- ✅ Mantener IDs manuales en Libro
- ✅ Añadir la relación @ManyToMany para favoritos

**Ventajas:**
- ✅ Sigue convención Java en nomenclatura
- ⏱️ Tiempo moderado (30-40 minutos)
- ✅ Funciona perfectamente
- 🟡 Mejor evaluación que Opción A

**Desventajas:**
- ⚠️ No coincide completamente con profesor
- 🟡 Modelo de datos todavía limitado

**Cuándo elegir esta opción:**
- Si quieres seguir convenciones pero no tienes mucho tiempo
- Si la relación @OneToOne te parece suficiente para el ejercicio
- Si priorizas la nomenclatura correcta

---

## RECOMENDACIÓN PERSONAL

Para decidir, hazte estas preguntas:

### ❓ ¿Vas a entregar este ejercicio al profesor?
- **SÍ** → Elige Opción B (seguir solución profesor)
- **NO** → Elige Opción A (mantener tu código)

### ❓ ¿Cuánto tiempo tienes disponible?
- **Mucho (2+ horas)** → Opción B
- **Moderado (30-60 min)** → Opción C
- **Poco (15 min)** → Opción A

### ❓ ¿Qué priorizas más?
- **Aprender correctamente** → Opción B
- **Funcionalidad rápida** → Opción A
- **Convenciones sin complicar** → Opción C

---

## MI RECOMENDACIÓN FINAL

Si vas a entregar esto al profesor: **OPCIÓN B**

**Razón:** El profesor claramente usa un estilo específico en sus soluciones. Seguir ese estilo:
1. Te garantiza mejor evaluación
2. Te enseña las convenciones correctas de Java
3. Te da un modelo de datos más realista
4. Te prepara mejor para proyectos futuros

El tiempo invertido (1-2 horas) vale la pena para:
- ✅ Aprender correctamente
- ✅ Obtener mejor calificación
- ✅ Tener código profesional

---

## SIGUIENTE PASO

Una vez decidas qué opción seguir, dime y:
1. **Opción A**: Usas el documento `EJERCICIO_MAPEO_MUCHOS_A_MUCHOS.md` tal como está
2. **Opción B**: Actualizo el documento y te ayudo con la refactorización completa
3. **Opción C**: Actualizo el documento solo con cambios de nomenclatura

**¿Qué opción prefieres?**
