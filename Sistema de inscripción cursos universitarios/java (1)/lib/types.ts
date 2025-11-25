export interface Horario {
  dia: string
  horaInicio: string
  horaFin: string
  salon: string
}

export interface Grupo {
  codigoGrupo: string
  profesorNombre: string
  cupoMaximo: number
  inscritosActuales: number
  horarios: Horario[]
}

export interface Curso {
  id: string
  codigo: string
  nombre: string
  programa: string
  periodo: string
  descripcion: string
  creditos: number
  activo: boolean
  esExtension: boolean
  costo: number
  inscripcionInicio: string
  inscripcionFin: string
  grupos: Grupo[]
}

export interface Usuario {
  id: string
  nombre: string
  apellido: string
  identificacion: string
  email: string
  rol: "ESTUDIANTE" | "ADMIN"
  programa: string
  semestre: number
}

export interface Inscripcion {
  id: string
  estudianteId: string
  cursoId: string
  codigoGrupo: string
  fechaInscripcion: string
  estado: string
  curso?: Curso
}

export interface Pago {
  id: string
  inscripcionId: string
  monto: number
  fechaPago: string
  estado: string
  referencia: string
}

export interface Notificacion {
  id: string
  destinatario: string
  asunto: string
  cuerpo: string
  estado: string
  fecha: string
}
