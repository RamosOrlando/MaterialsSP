-- Crear tipos de recursos si no existen
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'resource_type') THEN
        CREATE TYPE resource_type AS ENUM ('material', 'mano_de_obra', 'equipo');
    END IF;
END$$;

-- Tabla de Recursos Base
CREATE TABLE IF NOT EXISTS public."Resource" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" TEXT NOT NULL,
    "unit" TEXT NOT NULL, -- e.g., 'bolsa', 'kg', 'm3', 'HH', 'HM'
    "type" resource_type NOT NULL,
    "basePrice" NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

-- Tabla de Items de APU
CREATE TABLE IF NOT EXISTS public."ApuItem" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" TEXT NOT NULL UNIQUE,
    "unit" TEXT NOT NULL, -- e.g., 'm3', 'm2', 'pza', 'ml'
    "description" TEXT,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

-- Tabla de Composición de APUs (Rendimientos)
CREATE TABLE IF NOT EXISTS public."ApuComposition" (
    "itemId" UUID REFERENCES public."ApuItem"("id") ON DELETE CASCADE,
    "resourceId" UUID REFERENCES public."Resource"("id") ON DELETE RESTRICT,
    "yield" NUMERIC(12, 6) NOT NULL, -- Rendimiento
    PRIMARY KEY ("itemId", "resourceId")
);

-- Tabla de Proyectos (Para parametrizar el Formulario B-2)
CREATE TABLE IF NOT EXISTS public."Project" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" TEXT NOT NULL,
    "client" TEXT,
    "socialChargesPct" NUMERIC(5, 2) NOT NULL DEFAULT 55.00, -- e.g. 55%
    "ivaMoPct" NUMERIC(5, 2) NOT NULL DEFAULT 14.94,        -- e.g. 14.94%
    "toolsPct" NUMERIC(5, 2) NOT NULL DEFAULT 5.00,          -- e.g. 5%
    "generalExpensesPct" NUMERIC(5, 2) NOT NULL DEFAULT 10.00, -- e.g. 10%
    "utilityPct" NUMERIC(5, 2) NOT NULL DEFAULT 10.00,        -- e.g. 10%
    "taxItPct" NUMERIC(5, 2) NOT NULL DEFAULT 3.09,          -- e.g. 3.09% (IT)
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

-- Tabla de Items del Proyecto
CREATE TABLE IF NOT EXISTS public."ProjectItem" (
    "projectId" UUID REFERENCES public."Project"("id") ON DELETE CASCADE,
    "itemId" UUID REFERENCES public."ApuItem"("id") ON DELETE RESTRICT,
    "quantity" NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
    PRIMARY KEY ("projectId", "itemId")
);

-- Habilitar RLS
ALTER TABLE public."Resource" ENABLE ROW LEVEL SECURITY;
ALTER TABLE public."ApuItem" ENABLE ROW LEVEL SECURITY;
ALTER TABLE public."ApuComposition" ENABLE ROW LEVEL SECURITY;
ALTER TABLE public."Project" ENABLE ROW LEVEL SECURITY;
ALTER TABLE public."ProjectItem" ENABLE ROW LEVEL SECURITY;

-- Crear políticas básicas si no existen
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Permitir lectura para todos' AND tablename = 'Resource') THEN
        CREATE POLICY "Permitir lectura para todos" ON public."Resource" FOR SELECT USING (true);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Permitir lectura para todos' AND tablename = 'ApuItem') THEN
        CREATE POLICY "Permitir lectura para todos" ON public."ApuItem" FOR SELECT USING (true);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Permitir lectura para todos' AND tablename = 'ApuComposition') THEN
        CREATE POLICY "Permitir lectura para todos" ON public."ApuComposition" FOR SELECT USING (true);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Permitir lectura para todos' AND tablename = 'Project') THEN
        CREATE POLICY "Permitir lectura para todos" ON public."Project" FOR SELECT USING (true);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Permitir lectura para todos' AND tablename = 'ProjectItem') THEN
        CREATE POLICY "Permitir lectura para todos" ON public."ProjectItem" FOR SELECT USING (true);
    END IF;
END$$;

-- Insertar Datos Semilla Referenciales de Construcción en Bolivia
INSERT INTO public."Resource" ("name", "unit", "type", "basePrice") VALUES
-- Materiales
('Cemento Fancesa IP-40', 'bolsa', 'material', 55.0000),
('Cemento Viacha IP-40', 'bolsa', 'material', 58.0000),
('Fierro Corrugado 3/8"', 'kg', 'material', 10.5000),
('Arena Común', 'm3', 'material', 85.0000),
('Grava Seleccionada', 'm3', 'material', 110.0000),
('Madera de Construcción', 'pie2', 'material', 9.5000),
('Agua Potable', 'm3', 'material', 15.0000),
('Ladrillo Adobito', 'pza', 'material', 0.8500),
('Ladrillo 6 Huecos', 'pza', 'material', 1.3000),

-- Mano de Obra
('Albañil', 'HH', 'mano_de_obra', 25.0000),
('Ayudante', 'HH', 'mano_de_obra', 18.0000),
('Peón', 'HH', 'mano_de_obra', 15.0000),
('Especialista', 'HH', 'mano_de_obra', 35.0000),

-- Equipos
('Mezcladora de Hormigón', 'HM', 'equipo', 30.0000),
('Vibradora de Hormigón', 'HM', 'equipo', 18.0000),
('Guinche Eléctrico', 'HM', 'equipo', 22.0000)
ON CONFLICT DO NOTHING;
