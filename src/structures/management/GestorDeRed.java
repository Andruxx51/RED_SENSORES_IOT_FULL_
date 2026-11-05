package structures.management;

import model.LecturaSensor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GestorDeRed - Módulo Integrador de Estructuras de Datos
 * VERSION ESTUDIANTE
 *
 * Objetivo: Implementar la gestión de la red utilizando las colecciones de Java (JCF).
 * El éxito de este módulo no es solo que el código compile, sino que elijas
 * la estructura correcta para cada problema.
 */
public class GestorDeRed {
    // 1. Historial de Eventos: Registro lineal de todas las actividades
    private List<String> historialEventos;

    // 2. Navegador de Alertas: Permite moverse adelante y atrás entre alertas
    private Deque<String> navegadorAlertas;
    private String alertaActual;

    // 3. Ciclo de Monitoreo: Gestión de la rotación de sensores a consultar
    private Queue<String> colaMonitoreo;

    // 4. Topología de la Red: Grafo de conexiones entre estaciones (Lista de adyacencia)
    private Map<String, List<String>> topologiaRed;

    // 5. Buffer de Ingesta: Cola de espera para procesar lecturas entrantes
    private Queue<LecturaSensor> bufferIngesta;

    public GestorDeRed() {
        // TODO: Inicializa las colecciones usando las implementaciones más adecuadas de Java
        // (ArrayList, ArrayDeque, HashMap, etc.)
        this.historialEventos = new ArrayList<>();
        this.navegadorAlertas = new ArrayDeque<>();
        this.colaMonitoreo = new ArrayDeque<>();
        this.topologiaRed = new HashMap<>();
        this.bufferIngesta = new ArrayDeque<>();
    }

    // =========================================================================
    // SECCIÓN 1: HISTORIAL Y EVENTOS
    // =========================================================================
    /**
     * Reto 1: Registra un evento en el historial cronológico concatenando
     * la marca de tiempo actual con la descripción proporcionada.
     *
     * @param evento Descripción textual de lo ocurrido en la red.
     */
    public void agregarEvento(String evento) {
        // TODO 1: Implementado con fecha actual y descripción
        String conFecha = new Date() + " - " + evento;
        historialEventos.add(conFecha);
    }

    /**
     * Retorna una sublista con los eventos más recientes del historial.
     *
     * @param limite Número máximo de eventos recientes a consultar.
     * @return Lista con los últimos eventos registrados.
     */
    public List<String> obtenerEventosRecientes(int limite) {
        if (historialEventos.size() <= limite) return new ArrayList<>(historialEventos);
        return historialEventos.subList(historialEventos.size() - limite, historialEventos.size());
    }

    /**
     * Reto 10: Optimiza el uso de memoria en dispositivos IoT eliminando
     * los registros antiguos y conservando únicamente los últimos N eventos permitidos.
     *
     * @param mantener Cantidad máxima de registros recientes a conservar.
     */
    public void limpiarHistorialAntiguo(int mantener) {
        // TODO 10: Optimización para conservar solo los últimos 'mantener' eventos
        while (historialEventos.size() > mantener) {
            historialEventos.remove(0);
        }
    }

    // =========================================================================
    // SECCIÓN 2: NAVEGACIÓN DE ALERTAS
    // =========================================================================

    /**
     * Registra una nueva alerta en el navegador y actualiza el estado actual.
     *
     * @param alerta Mensaje de la alerta generada por la red.
     */
    public void registrarAlerta(String alerta) {
        navegadorAlertas.addLast(alerta);
        alertaActual = alerta;
    }

    /**
     * Reto 2: Desplaza el cursor hacia adelante en el historial de alertas usando un Deque,
     * implementando una rotación en ciclo continuo (pollFirst + addLast).
     *
     * @return La siguiente alerta en la secuencia o null si el navegador está vacío.
     */
    public String navegarAlSiguiente() {
        // TODO 2: Desplazamiento hacia adelante en el Deque
        if (navegadorAlertas.isEmpty()) return null;
        String alerta = navegadorAlertas.pollFirst();
        navegadorAlertas.addLast(alerta);
        alertaActual = alerta;
        return alerta;
    }

    /**
     * Reto 3: Desplaza el cursor hacia atrás en el historial de alertas usando un Deque,
     * implementando la rotación inversa en ciclo continuo (pollLast + addFirst).
     *
     * @return La alerta anterior en la secuencia o null si el navegador está vacío.
     */
    public String navegarAlAnterior() {
        // TODO 3: Desplazamiento hacia atrás en el Deque
        if (navegadorAlertas.isEmpty()) return null;
        String alerta = navegadorAlertas.pollLast();
        navegadorAlertas.addFirst(alerta);
        alertaActual = alerta;
        return alerta;
    }

    // =========================================================================
    // SECCIÓN 3: CICLO DE MONITOREO
    // =========================================================================

    /**
     * Reto 4: Programa la consulta de un sensor asegurando que no existan
     * identificadores duplicados en la cola de monitoreo.
     *
     * @param idSensor Identificador único de la estación a consultar.
     */
    public void programarConsulta(String idSensor) {
        // TODO 4: Evitar duplicados con contains()
        if (!colaMonitoreo.contains(idSensor)) {
            colaMonitoreo.add(idSensor);
        }
    }

    /**
     * Reto 5: Gestiona la rotación continua de la cola de sensores (FIFO),
     * extrayendo el sensor del frente y re-encolándolo inmediatamente al final.
     *
     * @return El identificador del siguiente sensor a consultar, o null si está vacía.
     */
    public String obtenerSiguienteSensor() {
        // TODO 5: Rotación del ciclo de monitoreo
        if (colaMonitoreo.isEmpty()) return null;
        String sensor = colaMonitoreo.poll();
        colaMonitoreo.add(sensor);
        return sensor;
    }


    // =========================================================================
    // SECCIÓN 4: TOPOLOGÍA DE RED
    // =========================================================================

    /**
     * Reto 6: Registra una conexión bidireccional entre dos estaciones de sensores
     * dentro del grafo de adyacencia, evitando duplicados en las listas de vecinos.
     *
     * @param sensorA Identificador del primer sensor.
     * @param sensorB Identificador del segundo sensor con el que se enlaza.
     */
    public void registrarConexion(String sensorA, String sensorB) {
        // TODO 6: Conexión bidireccional sin duplicados
        List<String> vecinosA = topologiaRed.computeIfAbsent(sensorA, k -> new ArrayList<>());
        if (!vecinosA.contains(sensorB)) vecinosA.add(sensorB);

        List<String> vecinosB = topologiaRed.computeIfAbsent(sensorB, k -> new ArrayList<>());
        if (!vecinosB.contains(sensorA)) vecinosB.add(sensorA);
    }

    /**
     * Reto 7: Consulta las conexiones directas de un sensor de forma segura
     * utilizando getOrDefault para evitar excepciones por claves inexistentes.
     *
     * @param idSensor Identificador del sensor a consultar.
     * @return Lista de sensores vecinos conectados, o una lista vacía si no posee conexiones.
     */
    public List<String> obtenerVecinos(String idSensor) {
        // TODO 7: Consulta segura con getOrDefault
        return topologiaRed.getOrDefault(idSensor, new ArrayList<>());
    }

    /**
     * Reto 9: Detecta nodos huérfanos o estaciones aisladas en la topología de red,
     * identificando aquellos sensores registrados que poseen listas de vecinos vacías.
     *
     * @return Lista con los identificadores de las estaciones que no tienen conexiones.
     */
    public List<String> detectarEstacionesAisladas() {
        // TODO 9: Detección de nodos sin vecinos conectados
        List<String> aisladas = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : topologiaRed.entrySet()) {
            if (entry.getValue().isEmpty()) {
                aisladas.add(entry.getKey());
            }
        }
        return aisladas;
    }

    // =========================================================================
    // SECCIÓN 5: BUFFER DE INGESTA
    // =========================================================================

    /**
     * Recibe y almacena una nueva lectura ambiental en el buffer de entrada.
     *
     * @param lectura Objeto LecturaSensor enviado por la estación de monitoreo.
     */
    public void recibirLectura(LecturaSensor lectura) {
        bufferIngesta.add(lectura);
    }

    /**
     * Reto 8: Procesa y extrae la lectura ambiental más antigua del buffer
     * bajo estricto orden FIFO mediante el uso seguro de poll().
     *
     * @return La siguiente LecturaSensor a procesar, o null si el buffer está vacío.
     */
    public LecturaSensor procesarSiguienteLectura() {
        // TODO 8: Extracción FIFO con poll()
        return bufferIngesta.poll();
    }

    /**
     * Extrae y filtra de manera eficiente aquellas lecturas del buffer
     * que superen un umbral de material particulado crítico utilizando Java Streams.
     *
     * @param limitePm25 Valor límite de PM2.5 para considerar una lectura como crítica.
     * @return Lista de lecturas que exceden el límite establecido.
     */
    public List<LecturaSensor> filtrarAlertasCriticas(double limitePm25) {
        // TODO EXTRA: Filtrado con Java Streams
        return bufferIngesta.stream()
                .filter(l -> l.getPm25() > limitePm25)
                .collect(Collectors.toList());
    }
}
