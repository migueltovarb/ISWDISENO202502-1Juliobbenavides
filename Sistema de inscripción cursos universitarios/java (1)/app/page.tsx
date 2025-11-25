"use client"

import React, { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { GraduationCap, Eye, EyeOff, Bell, CreditCard, CheckCircle, Clock, Trash2 } from "lucide-react"

// Mock Data Imports - Assuming these are correctly imported in your project structure
import type { Curso, Inscripcion, Notificacion, Pago } from "@/lib/types"
import { fetchJSON } from "@/lib/utils"
import { toast } from "@/hooks/use-toast"
import { Badge } from "@/components/ui/badge"

type AuthView = "login" | "register"

interface Usuario {
  id: string
  nombre: string
  apellido: string
  identificacion: string
  email: string
  rol: string
  programa: string
  semestre: number
}

export default function AuthPage() {
  const [view, setView] = useState<AuthView>("login")
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  // Login form state
  const [loginEmail, setLoginEmail] = useState("")
  const [loginIdentificacion, setLoginIdentificacion] = useState("")
  const [loginPassword, setLoginPassword] = useState("")

  // Register form state
  const [regNombre, setRegNombre] = useState("")
  const [regApellido, setRegApellido] = useState("")
  const [regIdentificacion, setRegIdentificacion] = useState("")
  const [regEmail, setRegEmail] = useState("")
  const [regPassword, setRegPassword] = useState("")
  const [regConfirmPassword, setRegConfirmPassword] = useState("")
  const [regRol, setRegRol] = useState("")
  const [regPrograma, setRegPrograma] = useState("")
  const [regSemestre, setRegSemestre] = useState("")

  // Authenticated user state
  const [user, setUser] = useState<Usuario | null>(null)

  useEffect(() => {
    fetchJSON<Usuario>(`/api/auth/me`).then((u) => setUser(u as unknown as Usuario)).catch(() => {})
  }, [])

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setLoading(true)

    try {
      if (!loginEmail || !loginPassword) {
        setError("Por favor complete todos los campos requeridos")
        return
      }
      const u = await fetchJSON<Usuario>(`/api/auth/login`, {
        method: "POST",
        body: JSON.stringify({ email: loginEmail, identificacion: loginIdentificacion, password: loginPassword }),
      })
      setUser(u as unknown as Usuario)
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Error al iniciar sesión. Intente de nuevo."
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    // Validaciones
    if (
      !regNombre ||
      !regApellido ||
      !regIdentificacion ||
      !regEmail ||
      !regPassword ||
      !regConfirmPassword ||
      !regRol ||
      !regPrograma ||
      !regSemestre
    ) {
      setError("Todos los campos son obligatorios")
      return
    }

    if (regPassword !== regConfirmPassword) {
      setError("Las contraseñas no coinciden")
      return
    }

    if (regPassword.length < 6) {
      setError("La contraseña debe tener al menos 6 caracteres")
      return
    }

    setLoading(true)

    try {
      const u = await fetchJSON<Usuario>(`/api/auth/register`, {
        method: "POST",
        body: JSON.stringify({
          nombre: regNombre,
          apellido: regApellido,
          identificacion: regIdentificacion,
          email: regEmail,
          password: regPassword,
          confirmPassword: regConfirmPassword,
          rol: regRol,
          programa: regPrograma,
          semestre: Number.parseInt(regSemestre),
        }),
      })
      setUser(u as unknown as Usuario)
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Error al registrarse. Intente de nuevo."
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    fetchJSON<void>(`/api/auth/logout`, { method: 'POST' }).catch(() => {})
    setUser(null)
    setLoginEmail("")
    setLoginPassword("")
    setLoginIdentificacion("")
  }

  // If the user is authenticated, show the Dashboard
  if (user) {
    return <Dashboard user={user} onLogout={handleLogout} />
  }

  return (
    <div className="min-h-screen bg-background flex flex-col">
      {/* Header */}
      <header className="bg-primary text-primary-foreground py-4 px-6">
        <div className="flex items-center gap-3 max-w-7xl mx-auto">
          <GraduationCap className="h-8 w-8" />
          <h1 className="text-xl font-semibold">Plataforma Universitaria</h1>
        </div>
      </header>

      {/* Auth Forms */}
      <main className="flex-1 flex items-center justify-center p-6">
        <div className="w-full max-w-md">
          {/* Tab Switcher */}
          <div className="flex mb-6">
            <button
              onClick={() => {
                setView("login")
                setError("")
              }}
              className={`flex-1 py-3 text-center font-medium border-b-2 transition-colors ${
                view === "login"
                  ? "border-primary text-primary"
                  : "border-transparent text-muted-foreground hover:text-foreground"
              }`}
            >
              Iniciar Sesión
            </button>
            <button
              onClick={() => {
                setView("register")
                setError("")
              }}
              className={`flex-1 py-3 text-center font-medium border-b-2 transition-colors ${
                view === "register"
                  ? "border-primary text-primary"
                  : "border-transparent text-muted-foreground hover:text-foreground"
              }`}
            >
              Registrarse
            </button>
          </div>

          {/* Error Message */}
          {error && (
            <div className="bg-destructive/10 border border-destructive/50 text-destructive px-4 py-3 rounded-lg mb-4">
              {error}
            </div>
          )}

          {/* Login Form */}
          {view === "login" && (
            <form onSubmit={handleLogin} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="login-email">Correo Electrónico *</Label>
                <Input
                  id="login-email"
                  type="email"
                  placeholder="correo@universidad.edu"
                  value={loginEmail}
                  onChange={(e) => setLoginEmail(e.target.value)}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="login-identificacion">Identificación</Label>
                <Input
                  id="login-identificacion"
                  type="text"
                  placeholder="Número de identificación"
                  value={loginIdentificacion}
                  onChange={(e) => setLoginIdentificacion(e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="login-password">Contraseña *</Label>
                <div className="relative">
                  <Input
                    id="login-password"
                    type={showPassword ? "text" : "password"}
                    placeholder="••••••••"
                    value={loginPassword}
                    onChange={(e) => setLoginPassword(e.target.value)}
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  >
                    {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? "Iniciando sesión..." : "Iniciar Sesión"}
              </Button>

              <p className="text-center text-sm text-muted-foreground">
                ¿No tienes cuenta?{" "}
                <button type="button" onClick={() => setView("register")} className="text-primary hover:underline">
                  Regístrate aquí
                </button>
              </p>
            </form>
          )}

          {/* Register Form */}
          {view === "register" && (
            <form onSubmit={handleRegister} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="reg-nombre">Nombre *</Label>
                  <Input
                    id="reg-nombre"
                    type="text"
                    placeholder="Juan"
                    value={regNombre}
                    onChange={(e) => setRegNombre(e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="reg-apellido">Apellido *</Label>
                  <Input
                    id="reg-apellido"
                    type="text"
                    placeholder="Pérez"
                    value={regApellido}
                    onChange={(e) => setRegApellido(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="reg-identificacion">Identificación *</Label>
                <Input
                  id="reg-identificacion"
                  type="text"
                  placeholder="Número de identificación"
                  value={regIdentificacion}
                  onChange={(e) => setRegIdentificacion(e.target.value)}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="reg-email">Correo Electrónico *</Label>
                <Input
                  id="reg-email"
                  type="email"
                  placeholder="correo@universidad.edu"
                  value={regEmail}
                  onChange={(e) => setRegEmail(e.target.value)}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="reg-rol">Rol *</Label>
                <Select value={regRol} onValueChange={setRegRol} required>
                  <SelectTrigger id="reg-rol">
                    <SelectValue placeholder="Selecciona un rol" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ESTUDIANTE">Estudiante</SelectItem>
                    <SelectItem value="ADMIN">Administrador</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="reg-programa">Programa *</Label>
                <Select value={regPrograma} onValueChange={setRegPrograma} required>
                  <SelectTrigger id="reg-programa">
                    <SelectValue placeholder="Selecciona un programa" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Ingeniería de Sistemas">Ingeniería de Sistemas</SelectItem>
                    <SelectItem value="Ingeniería Electrónica">Ingeniería Electrónica</SelectItem>
                    <SelectItem value="Ingeniería Industrial">Ingeniería Industrial</SelectItem>
                    <SelectItem value="Administración de Empresas">Administración de Empresas</SelectItem>
                    <SelectItem value="Contaduría Pública">Contaduría Pública</SelectItem>
                    <SelectItem value="Derecho">Derecho</SelectItem>
                    <SelectItem value="Psicología">Psicología</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="reg-semestre">Semestre *</Label>
                <Select value={regSemestre} onValueChange={setRegSemestre} required>
                  <SelectTrigger id="reg-semestre">
                    <SelectValue placeholder="Selecciona el semestre" />
                  </SelectTrigger>
                  <SelectContent>
                    {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((sem) => (
                      <SelectItem key={sem} value={sem.toString()}>
                        {sem}° Semestre
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="reg-password">Contraseña *</Label>
                <div className="relative">
                  <Input
                    id="reg-password"
                    type={showPassword ? "text" : "password"}
                    placeholder="Mínimo 6 caracteres"
                    value={regPassword}
                    onChange={(e) => setRegPassword(e.target.value)}
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  >
                    {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="reg-confirm-password">Confirmar Contraseña *</Label>
                <div className="relative">
                  <Input
                    id="reg-confirm-password"
                    type={showConfirmPassword ? "text" : "password"}
                    placeholder="Repite la contraseña"
                    value={regConfirmPassword}
                    onChange={(e) => setRegConfirmPassword(e.target.value)}
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  >
                    {showConfirmPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? "Registrando..." : "Registrarse"}
              </Button>

              <p className="text-center text-sm text-muted-foreground">
                ¿Ya tienes cuenta?{" "}
                <button type="button" onClick={() => setView("login")} className="text-primary hover:underline">
                  Inicia sesión
                </button>
              </p>
            </form>
          )}
        </div>
      </main>
    </div>
  )
}

function Dashboard({ user, onLogout }: { user: Usuario; onLogout: () => void }) {
  // Renderizar interfaz según el rol
  if (user.rol === "ADMIN") {
    return <AdminDashboard user={user} onLogout={onLogout} />
  }

  return <StudentDashboard user={user} onLogout={onLogout} />
}

function AdminDashboard({ user, onLogout }: { user: Usuario; onLogout: () => void }) {
  const [activeTab, setActiveTab] = useState<"cursos" | "reportes">("cursos")
  const [cursos, setCursos] = useState<Curso[]>([])
  const [editingCurso, setEditingCurso] = useState<Curso | null>(null)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [totalInscripciones, setTotalInscripciones] = useState<number>(0)

  useEffect(() => {
    fetchJSON<Curso[]>(`/api/cursos`).then(setCursos).catch(() => {})
    fetchJSON<{ total: number }>(`/api/inscripciones/count`).then((d) => setTotalInscripciones(d?.total || 0)).catch(() => {})
  }, [])

  const handleCrearCurso = () => {
    setShowCreateForm(true)
    setEditingCurso(null)
  }

  const handleEditarCurso = (curso: Curso) => {
    setEditingCurso(curso)
    setShowCreateForm(true)
  }

  const handleEliminarCurso = (id: string) => {
    if (confirm("¿Estás seguro de eliminar este curso?")) {
      setCursos(cursos.filter((c) => c.id !== id))
    }
  }

  const handleToggleActivo = async (id: string) => {
    const curso = cursos.find((c) => c.id === id)
    if (!curso) return
    try {
      await fetchJSON<void>(`/api/cursos/${id}/${curso.activo ? "desactivar" : "activar"}`, { method: "PATCH" })
      const refreshed = await fetchJSON<Curso[]>(`/api/cursos`)
      setCursos(refreshed)
      if (typeof window !== "undefined") {
        try {
          window.dispatchEvent(new CustomEvent("coursesUpdated"))
          localStorage.setItem("courses_updated_at", String(Date.now()))
        } catch {}
      }
    } catch (e) {
      const msg = e instanceof Error ? e.message : "Error al actualizar estado del curso"
      alert(msg)
    }
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="bg-primary text-primary-foreground py-3 px-6">
        <div className="flex items-center gap-3 max-w-7xl mx-auto">
          <GraduationCap className="h-6 w-6" />
          <h1 className="text-lg font-semibold">Plataforma Universitaria - Panel Administrativo</h1>
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-muted/50 border-b px-6 py-3">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex gap-2">
            <Button
              variant={activeTab === "cursos" ? "default" : "outline"}
              size="sm"
              onClick={() => setActiveTab("cursos")}
            >
              Gestión de Cursos
            </Button>
            <Button
              variant={activeTab === "reportes" ? "default" : "outline"}
              size="sm"
              onClick={() => setActiveTab("reportes")}
            >
              Reportes
            </Button>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" size="sm" onClick={onLogout}>
              Cerrar Sesión ({user.nombre})
            </Button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto p-6">
        {activeTab === "cursos" && (
          <div>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-accent">Gestión de Cursos</h2>
              <Button onClick={handleCrearCurso}>Crear Nuevo Curso</Button>
            </div>

            {showCreateForm && (
              <CursoForm
                curso={editingCurso}
                onSave={async (curso) => {
                  try {
                    if (editingCurso) {
                      await fetchJSON<Curso>(`/api/cursos/${curso.id}`, { method: "PUT", body: JSON.stringify(curso) })
                    } else {
                      const payload = { ...curso } as any
                      delete payload.id
                      const created = await fetchJSON<Curso>(`/api/cursos`, { method: "POST", body: JSON.stringify(payload) })
                      curso = created
                    }
                    const refreshed = await fetchJSON<Curso[]>(`/api/cursos`)
                    setCursos(refreshed)
                  } catch {}
                  setShowCreateForm(false)
                  setEditingCurso(null)
                }}
                onCancel={() => {
                  setShowCreateForm(false)
                  setEditingCurso(null)
                }}
              />
            )}

            <div className="grid gap-4 mt-6">
              {cursos.map((curso) => (
                <div key={curso.id} className="bg-card rounded-lg border p-4">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <h3 className="font-semibold text-lg">{curso.nombre}</h3>
                        <Badge variant={curso.activo ? "default" : "secondary"}>
                          {curso.activo ? "Activo" : "Inactivo"}
                        </Badge>
                        {curso.esExtension && <Badge variant="outline">Extensión</Badge>}
                      </div>
                      <p className="text-sm text-muted-foreground mb-3">
                        {curso.codigo} | {curso.programa} | {curso.periodo}
                      </p>
                      <div className="space-y-2">
                        {curso.grupos.map((grupo) => (
                          <div key={grupo.codigoGrupo} className="bg-muted/50 rounded p-3 text-sm">
                            <div className="grid grid-cols-2 gap-2">
                              <div>
                                <span className="font-medium">Grupo:</span> {grupo.codigoGrupo}
                              </div>
                              <div>
                                <span className="font-medium">Profesor:</span> {grupo.profesorNombre}
                              </div>
                              <div>
                                <span className="font-medium">Cupos:</span> {grupo.inscritosActuales}/{grupo.cupoMaximo}
                              </div>
                              <div>
                                <span className="font-medium">Horario:</span>{" "}
                                {grupo.horarios
                                  .map((h) => `${h.dia.substring(0, 3)} ${h.horaInicio}-${h.horaFin}`)
                                  .join(", ")}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                    <div className="flex gap-2 ml-4">
                      <Button size="sm" variant="outline" onClick={() => handleToggleActivo(curso.id)}>
                        {curso.activo ? "Desactivar" : "Activar"}
                      </Button>
                      <Button size="sm" variant="outline" onClick={() => handleEditarCurso(curso)}>
                        Editar
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => handleEliminarCurso(curso.id)}>
                        Eliminar
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        

        {activeTab === "reportes" && (
          <div>
            <h2 className="text-2xl font-bold text-accent mb-6">Reportes y Estadísticas</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
              <div className="bg-card rounded-lg border p-6">
                <h3 className="text-sm text-muted-foreground mb-2">Total Cursos</h3>
                <p className="text-3xl font-bold">{cursos.length}</p>
              </div>
              <div className="bg-card rounded-lg border p-6">
                <h3 className="text-sm text-muted-foreground mb-2">Total Inscripciones</h3>
                <p className="text-3xl font-bold">{totalInscripciones}</p>
              </div>
              <div className="bg-card rounded-lg border p-6">
                <h3 className="text-sm text-muted-foreground mb-2">Cursos Activos</h3>
                <p className="text-3xl font-bold">{cursos.filter((c) => c.activo).length}</p>
              </div>
            </div>
            <div className="bg-card rounded-lg border p-6">
              <h3 className="font-semibold mb-4">Distribución por Programa</h3>
              <div className="space-y-2">
                {Array.from(new Set(cursos.map((c) => c.programa))).map((programa) => (
                  <div key={programa} className="flex items-center justify-between">
                    <span>{programa}</span>
                    <span className="font-semibold">{cursos.filter((c) => c.programa === programa).length} cursos</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

function CursoForm({
  curso,
  onSave,
  onCancel,
}: {
  curso: Curso | null
  onSave: (curso: Curso) => void
  onCancel: () => void
}) {
  const [formData, setFormData] = useState<Curso>(
    curso || {
      id: "",
      codigo: "",
      nombre: "",
      programa: "",
      periodo: "",
      descripcion: "",
      creditos: 0,
      esExtension: false,
      costo: 0,
      activo: true,
      grupos: [],
    },
  )

  const [grupoInicial, setGrupoInicial] = useState<{ codigoGrupo: string; profesorNombre: string; cupoMaximo: number }>(
    { codigoGrupo: "A", profesorNombre: "", cupoMaximo: 30 },
  )
  const [horario, setHorario] = useState<{ dia: string; horaInicio: string; horaFin: string; salon: string }>({
    dia: "Lunes",
    horaInicio: "08:00",
    horaFin: "10:00",
    salon: "Por asignar",
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    let next = { ...formData }
    if (!next.grupos || next.grupos.length === 0) {
      next.grupos = [
        {
          codigoGrupo: grupoInicial.codigoGrupo || "A",
          profesorNombre: grupoInicial.profesorNombre || "Por asignar",
          cupoMaximo: grupoInicial.cupoMaximo || 30,
          inscritosActuales: 0,
          horarios: [horario],
        },
      ]
    }
    if (next.esExtension && (!next.costo || next.costo <= 0)) {
      next.costo = 500000
    }
    onSave(next)
  }

  return (
    <div className="bg-card rounded-lg border p-6 mb-6">
      <h3 className="text-xl font-semibold mb-4">{curso ? "Editar Curso" : "Crear Nuevo Curso"}</h3>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <Label htmlFor="codigo">Código</Label>
            <Input
              id="codigo"
              value={formData.codigo}
              onChange={(e) => setFormData({ ...formData, codigo: e.target.value })}
              required
            />
          </div>
          <div>
            <Label htmlFor="nombre">Nombre</Label>
            <Input
              id="nombre"
              value={formData.nombre}
              onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
              required
            />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <Label htmlFor="programa">Programa</Label>
            <Input
              id="programa"
              value={formData.programa}
              onChange={(e) => setFormData({ ...formData, programa: e.target.value })}
              required
            />
          </div>
          <div>
            <Label htmlFor="periodo">Periodo</Label>
            <Input
              id="periodo"
              value={formData.periodo}
              onChange={(e) => setFormData({ ...formData, periodo: e.target.value })}
              required
            />
          </div>
        </div>
        <div className="grid grid-cols-4 gap-4">
          <div>
            <Label htmlFor="dia">Día</Label>
            <Select value={horario.dia} onValueChange={(v) => setHorario({ ...horario, dia: v })}>
              <SelectTrigger id="dia">
                <SelectValue placeholder="Día" />
              </SelectTrigger>
              <SelectContent>
                {[
                  "Lunes",
                  "Martes",
                  "Miércoles",
                  "Jueves",
                  "Viernes",
                  "Sábado",
                ].map((d) => (
                  <SelectItem key={d} value={d}>
                    {d}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div>
            <Label htmlFor="horaInicio">Hora inicio</Label>
            <Input id="horaInicio" value={horario.horaInicio} onChange={(e) => setHorario({ ...horario, horaInicio: e.target.value })} />
          </div>
          <div>
            <Label htmlFor="horaFin">Hora fin</Label>
            <Input id="horaFin" value={horario.horaFin} onChange={(e) => setHorario({ ...horario, horaFin: e.target.value })} />
          </div>
          <div>
            <Label htmlFor="salon">Salón</Label>
            <Input id="salon" value={horario.salon} onChange={(e) => setHorario({ ...horario, salon: e.target.value })} />
          </div>
        </div>
        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            id="esExtension"
            checked={formData.esExtension}
            onChange={(e) => setFormData({ ...formData, esExtension: e.target.checked })}
            className="h-4 w-4"
          />
          <Label htmlFor="esExtension">Es curso de extensión</Label>
        </div>
        {formData.esExtension && (
          <div>
            <Label htmlFor="costo">Costo</Label>
            <Input id="costo" type="number" value={formData.costo || 0} onChange={(e) => setFormData({ ...formData, costo: Number(e.target.value) })} />
          </div>
        )}
        <div className="grid grid-cols-3 gap-4">
          <div>
            <Label htmlFor="grupo-codigo">Código Grupo</Label>
            <Input id="grupo-codigo" value={grupoInicial.codigoGrupo} onChange={(e) => setGrupoInicial({ ...grupoInicial, codigoGrupo: e.target.value })} />
          </div>
          <div>
            <Label htmlFor="grupo-prof">Profesor</Label>
            <Input id="grupo-prof" value={grupoInicial.profesorNombre} onChange={(e) => setGrupoInicial({ ...grupoInicial, profesorNombre: e.target.value })} />
          </div>
          <div>
            <Label htmlFor="grupo-cupo">Cupo Máximo</Label>
            <Input id="grupo-cupo" type="number" value={grupoInicial.cupoMaximo} onChange={(e) => setGrupoInicial({ ...grupoInicial, cupoMaximo: Number(e.target.value) })} />
          </div>
        </div>
        <div className="flex gap-2 justify-end">
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancelar
          </Button>
          <Button type="submit">Guardar</Button>
        </div>
      </form>
    </div>
  )
}

function StudentDashboard({ user, onLogout }: { user: Usuario; onLogout: () => void }) {
  const [activeTab, setActiveTab] = useState<"disponibles" | "inscripciones" | "pagos" | "notificaciones">(
    "disponibles",
  )
  const [searchTerm, setSearchTerm] = useState("")
  const [programa, setPrograma] = useState("")
  const [periodo, setPeriodo] = useState("")
  const [tipo, setTipo] = useState("")
  const [inscripciones, setInscripciones] = useState<Inscripcion[]>([])
  const [notificaciones, setNotificaciones] = useState<Notificacion[]>([])
  const [pagos, setPagos] = useState<Pago[]>([])
  const [cursos, setCursos] = useState<Curso[]>([])

  const loadCursos = () => {
    const params = new URLSearchParams()
    if (programa) params.set("programa", programa)
    if (periodo) params.set("periodo", periodo)
    if (searchTerm) params.set("q", searchTerm)
    if (tipo) params.set("tipo", tipo)
    fetchJSON<Curso[]>(`/api/cursos${params.toString() ? `?${params.toString()}` : ""}`)
      .then(setCursos)
      .catch(() => {})
  }

  useEffect(() => {
    loadCursos()
  }, [programa, periodo, searchTerm, tipo])

  useEffect(() => {
    const onUpdated = () => loadCursos()
    const onStorage = (e: StorageEvent) => {
      if (e.key === "courses_updated_at") loadCursos()
    }
    if (typeof window !== "undefined") {
      window.addEventListener("coursesUpdated", onUpdated)
      window.addEventListener("storage", onStorage)
    }
    return () => {
      if (typeof window !== "undefined") {
        window.removeEventListener("coursesUpdated", onUpdated)
        window.removeEventListener("storage", onStorage)
      }
    }
  }, [])

  useEffect(() => {
    if (!user?.id) return
    fetchJSON<Inscripcion[]>(`/api/inscripciones?estudianteId=${encodeURIComponent(user.id)}`)
      .then((list) => {
        const withCurso = list.map((i) => ({ ...i, curso: cursos.find((c) => c.id === i.cursoId) }))
        setInscripciones(withCurso)
      })
      .catch(() => {})
  }, [user?.id, cursos])

  useEffect(() => {
    if (!user?.id) return
    fetchJSON<Pago[]>(`/api/pagos?estudianteId=${encodeURIComponent(user.id)}`)
      .then(setPagos)
      .catch(() => {})
  }, [user?.id])

  useEffect(() => {
    if (!user?.email) return
    fetchJSON<Notificacion[]>(`/api/notificaciones?email=${encodeURIComponent(user.email)}`)
      .then(setNotificaciones)
      .catch(() => {})
  }, [user?.email])

  const filteredCursos = cursos.filter((curso) => {
    const matchesSearch =
      curso.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      curso.codigo.toLowerCase().includes(searchTerm.toLowerCase())
    const normalize = (s: string) => s.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase()
    const matchesPrograma = !programa || programa === "todos" || normalize(curso.programa).includes(normalize(programa))
    const matchesPeriodo = !periodo || periodo === "todos" || curso.periodo === periodo
    const matchesTipo = !tipo || tipo === "todos" || (tipo === "extension" ? curso.esExtension : !curso.esExtension)
    return matchesSearch && matchesPrograma && matchesPeriodo && matchesTipo
  })

  const handleInscribir = async (curso: Curso, codigoGrupo: string) => {
    try {
      const ins = await fetchJSON<Inscripcion>(`/api/inscripciones`, {
        method: "POST",
        body: JSON.stringify({ usuarioId: user.id, cursoId: curso.id, codigoGrupo }),
      })
      setInscripciones([...inscripciones, { ...ins, curso }])
      // si requiere pago, llevar a pestaña Pagos
      if (ins.estado === "PENDIENTE_PAGO") {
        setActiveTab("pagos")
        toast({ title: "Inscripción registrada", description: `Curso ${curso.nombre} pendiente de pago.` })
      } else {
        setActiveTab("inscripciones")
        toast({ title: "Inscripción confirmada", description: `Te inscribiste en ${curso.nombre}.` })
      }
      // refrescar notificaciones
      fetchJSON<Notificacion[]>(`/api/notificaciones?email=${encodeURIComponent(user.email)}`).then(setNotificaciones).catch(() => {})
    } catch {}
  }

  const handleCancelarInscripcion = async (id: string) => {
    try {
      await fetchJSON<Inscripcion>(`/api/inscripciones/cancelar`, { method: "POST", body: JSON.stringify({ inscripcionId: id }) })
      setInscripciones(inscripciones.filter((i) => i.id !== id))
    } catch {}
  }

  const handlePagarInscripcion = async (inscripcionId: string) => {
    try {
      const pago = await fetchJSON<Pago>(`/api/pagos/pagar`, { method: "POST", body: JSON.stringify({ inscripcionId }) })
      setPagos([...pagos, pago])
      // actualizar estado de la inscripción a ACTIVA
      setInscripciones(inscripciones.map((i) => (i.id === inscripcionId ? { ...i, estado: "ACTIVA" } : i)))
      // refrescar notificaciones de pago
      fetchJSON<Notificacion[]>(`/api/notificaciones?email=${encodeURIComponent(user.email)}`).then(setNotificaciones).catch(() => {})
      toast({ title: "Pago aprobado", description: `Referencia ${pago.referencia} por $${pago.monto?.toLocaleString()}.` })
    } catch {}
  }

  const handleMarcarLeida = (id: string) => {
    setNotificaciones(notificaciones.map((n) => (n.id === id ? { ...n, leida: true } : n)))
  }

  const handleEliminarNotificacion = (id: string) => {
    setNotificaciones(notificaciones.filter((n) => n.id !== id))
  }

  const formatHorario = (horarios: { dia: string; horaInicio: string; horaFin: string; salon: string }[]) => {
    return horarios
      .map((h) => `${h.dia.substring(0, 5)} ${h.horaInicio}-${h.horaFin}${h.salon ? ` (${h.salon})` : ""}`)
      .join(" / ")
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="bg-primary text-primary-foreground py-3 px-6">
        <div className="flex items-center gap-3 max-w-7xl mx-auto">
          <GraduationCap className="h-6 w-6" />
          <h1 className="text-lg font-semibold">Plataforma Universitaria</h1>
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-muted/50 border-b px-6 py-3">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex gap-2">
            <Button
              variant={activeTab === "disponibles" ? "default" : "outline"}
              size="sm"
              onClick={() => setActiveTab("disponibles")}
            >
              Cursos
            </Button>
            <Button
              variant={activeTab === "inscripciones" ? "default" : "outline"}
              size="sm"
              onClick={() => setActiveTab("inscripciones")}
            >
              Mis Inscripciones
            </Button>
            <Button
              variant={activeTab === "pagos" ? "default" : "outline"}
              size="sm"
              onClick={() => setActiveTab("pagos")}
            >
              <CreditCard className="h-4 w-4 mr-1" />
              Pagos
            </Button>
          </div>
          <div className="flex gap-2">
            <Button
              variant={activeTab === "notificaciones" ? "default" : "outline"}
              size="sm"
              onClick={() => setActiveTab("notificaciones")}
              className="relative"
            >
              <Bell className="h-4 w-4 mr-1" />
              Notificaciones
              {notificaciones.length > 0 && (
                <span className="absolute -top-1 -right-1 bg-destructive text-destructive-foreground text-xs rounded-full h-5 w-5 flex items-center justify-center">
                  {notificaciones.length}
                </span>
              )}
            </Button>
            <Button variant="outline" size="sm" onClick={onLogout}>
              Cerrar Sesión ({user.nombre})
            </Button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto p-6">
        {/* Cursos Disponibles */}
        {activeTab === "disponibles" && (
          <div>
            <h2 className="text-2xl font-bold text-accent mb-6">Cursos Disponibles</h2>

            {/* Filtros */}
            <div className="flex items-end gap-3 mb-6">
              <div className="w-40">
                <Label htmlFor="programa-filter">Programa</Label>
                <Select value={programa} onValueChange={setPrograma}>
                  <SelectTrigger id="programa-filter">
                    <SelectValue placeholder="Programa" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="todos">Todos</SelectItem>
                    <SelectItem value="Ingeniería de Software">Ingeniería de Software</SelectItem>
                    <SelectItem value="Ingeniería de Sistemas">Ingeniería de Sistemas</SelectItem>
                    <SelectItem value="Ingeniería Electrónica">Ingeniería Electrónica</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="w-36">
                <Label htmlFor="periodo-filter">Periodo</Label>
                <Select value={periodo} onValueChange={setPeriodo}>
                  <SelectTrigger id="periodo-filter">
                    <SelectValue placeholder="Periodo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="todos">Todos</SelectItem>
                    <SelectItem value="2025-1">2025-1</SelectItem>
                    <SelectItem value="2024-2">2024-2</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="flex-1">
                <Label htmlFor="search-filter">Código o nombre</Label>
                <Input
                  id="search-filter"
                  placeholder="Código o nombre"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              <div className="w-36">
                <Label htmlFor="tipo-filter">Tipo</Label>
                <Select value={tipo} onValueChange={setTipo}>
                  <SelectTrigger id="tipo-filter">
                    <SelectValue placeholder="Tipo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="todos">Todos</SelectItem>
                    <SelectItem value="regular">Regular</SelectItem>
                    <SelectItem value="extension">Extensión</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <Button onClick={loadCursos}>Buscar</Button>
            </div>

            {/* Lista de Cursos */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {filteredCursos.map((curso) => (
                <div key={curso.id} className="bg-card rounded-lg border p-4">
                  <div className="mb-3">
                    <h3 className="font-semibold text-lg mb-1">{curso.nombre}</h3>
                    <p className="text-sm text-muted-foreground">CURSO: {curso.codigo}</p>
                  </div>

                  {curso.grupos.map((grupo) => (
                    <div key={grupo.codigoGrupo} className="bg-muted/30 rounded p-3 mb-2">
                      <div className="space-y-1 text-sm mb-2">
                        <p>
                          <span className="font-medium">PROFESOR:</span> {grupo.profesorNombre}
                        </p>
                        <p>
                          <span className="font-medium">HORARIO:</span> {formatHorario(grupo.horarios)}
                        </p>
                        <p>
                          <span className="font-medium">CUPOS:</span> {grupo.inscritosActuales}/{grupo.cupoMaximo}
                        </p>
                        <p>
                          <span className="font-medium">ESTADO:</span>{" "}
                          {curso.activo ? (
                            <Badge variant="default" className="bg-green-600">Disponible</Badge>
                          ) : (
                            <Badge variant="destructive" className="bg-red-600">No disponible</Badge>
                          )}
                        </p>
                      </div>
                      <Button
                        size="sm"
                        className="w-full bg-green-600 hover:bg-green-700"
                        onClick={() => handleInscribir(curso, grupo.codigoGrupo)}
                        disabled={!curso.activo || grupo.inscritosActuales >= grupo.cupoMaximo}
                      >
                        Inscribir
                      </Button>
                    </div>
                  ))}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Mis Inscripciones */}
        {activeTab === "inscripciones" && (
          <div>
            <h2 className="text-2xl font-bold text-accent mb-6">Mis Inscripciones</h2>
            {inscripciones.length === 0 ? (
              <div className="text-center py-12 text-muted-foreground">No tienes inscripciones activas</div>
            ) : (
              <div className="space-y-3">
                {inscripciones.map((inscripcion) => (
                  <div key={inscripcion.id} className="bg-card rounded-lg border p-4">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <h3 className="font-semibold text-lg mb-1">{inscripcion.curso?.nombre ?? inscripcion.cursoId}</h3>
                        <p className="text-sm text-muted-foreground mb-2">
                          Grupo {inscripcion.codigoGrupo} | {inscripcion.curso?.codigo ?? inscripcion.cursoId}
                        </p>
                        <div className="flex items-center gap-2">
                          {inscripcion.estado === "PENDIENTE_PAGO" ? (
                            <Badge variant="secondary" className="bg-yellow-600">
                              <Clock className="h-3 w-3 mr-1" />
                              Pendiente de Pago
                            </Badge>
                          ) : (
                            <Badge variant="default" className="bg-green-600">
                              <CheckCircle className="h-3 w-3 mr-1" />
                              Confirmada
                            </Badge>
                          )}
                          <span className="text-xs text-muted-foreground">
                            {new Date(inscripcion.fechaInscripcion).toLocaleDateString()}
                          </span>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {inscripcion.estado === "PENDIENTE_PAGO" ? (
                          <Button size="sm" variant="outline" onClick={() => setActiveTab("pagos")}>Ir a pagar</Button>
                        ) : (
                          <Button size="sm" variant="outline" disabled>
                            Confirmada
                          </Button>
                        )}
                        <Button size="sm" variant="destructive" onClick={() => handleCancelarInscripcion(inscripcion.id)}>
                          Cancelar
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Pagos */}
        {activeTab === "pagos" && (
          <div>
            <h2 className="text-2xl font-bold text-accent mb-6">Pagos</h2>
            {/* Pendientes por pagar */}
            <h3 className="font-semibold mb-4">Pendientes de Pago</h3>
            <div className="space-y-3 mb-6">
              {inscripciones.filter((i) => i.estado === "PENDIENTE_PAGO").length === 0 && (
                <div className="text-muted-foreground">No tienes pagos pendientes</div>
              )}
              {inscripciones
                .filter((i) => i.estado === "PENDIENTE_PAGO")
                .map((inscripcion) => (
                  <div key={inscripcion.id} className="bg-card rounded-lg border p-4">
                    <div className="flex items-center justify-between">
                      <div>
                        <h4 className="font-semibold">{inscripcion.curso?.nombre ?? inscripcion.cursoId}</h4>
                        <p className="text-sm text-muted-foreground">Referencia pendiente</p>
                      </div>
                      <div className="flex items-center gap-3">
                        <Badge variant="secondary" className="bg-yellow-600">Pendiente</Badge>
                        <Button size="sm" onClick={() => handlePagarInscripcion(inscripcion.id)}>
                          Pagar ahora
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
            </div>

            {/* Historial de pagos */}
            <h3 className="font-semibold mb-4">Historial de Pagos</h3>
            <div className="space-y-3">
              {pagos.length === 0 && <div className="text-muted-foreground">Sin pagos registrados</div>}
              {pagos.map((pago) => (
                <div key={pago.id} className="bg-card rounded-lg border p-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-semibold">{inscripciones.find((i) => i.id === pago.inscripcionId)?.curso?.nombre ?? pago.inscripcionId}</h4>
                      <p className="text-sm text-muted-foreground">{new Date(pago.fechaPago).toLocaleDateString()}</p>
                    </div>
                    <div className="text-right">
                      <p className="font-semibold text-lg">${pago.monto?.toLocaleString()}</p>
                      <Badge variant={pago.estado === "APROBADA" ? "default" : "destructive"} className={pago.estado === "APROBADA" ? "bg-green-600" : "bg-red-600"}>
                        {pago.estado === "APROBADA" ? <CheckCircle className="h-3 w-3 mr-1" /> : <Clock className="h-3 w-3 mr-1" />}
                        {pago.estado}
                      </Badge>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Notificaciones */}
        {activeTab === "notificaciones" && (
          <div>
            <h2 className="text-2xl font-bold text-accent mb-6">Notificaciones</h2>
            {notificaciones.length === 0 ? (
              <div className="text-center py-12 text-muted-foreground">No tienes notificaciones</div>
            ) : (
              <div className="space-y-3">
                {notificaciones.map((notif) => {
                  const lowerAsunto = (notif.asunto || "").toLowerCase()
                  const lowerCuerpo = (notif.cuerpo || "").toLowerCase()
                  let label = notif.estado
                  let variant: "default" | "secondary" | "destructive" | "outline" = "outline"
                  let extraClass = ""
                  if (lowerAsunto.includes("inscripción confirmada") || lowerCuerpo.includes("activa")) {
                    label = "Confirmada"
                    variant = "default"
                    extraClass = "bg-green-600"
                  } else if (lowerAsunto.includes("confirmación de pago") || lowerCuerpo.includes("pago aprobado")) {
                    label = "Pago aprobado"
                    variant = "default"
                    extraClass = "bg-green-600"
                  } else if (lowerAsunto.includes("pago pendiente") || lowerCuerpo.includes("pendiente_pago")) {
                    label = "Pendiente de pago"
                    variant = "secondary"
                    extraClass = "bg-yellow-600"
                  }
                  return (
                    <div key={notif.id} className={`bg-card rounded-lg border p-4`}>
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-2">
                            <h3 className="font-semibold">{notif.asunto}</h3>
                          </div>
                          <p className="text-sm text-muted-foreground mb-2">{notif.cuerpo}</p>
                          <p className="text-xs text-muted-foreground">{new Date(notif.fecha).toLocaleString()}</p>
                        </div>
                        <div className="flex gap-2 ml-4">
                          <Badge variant={variant} className={extraClass}>{label}</Badge>
                        </div>
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  )
}
