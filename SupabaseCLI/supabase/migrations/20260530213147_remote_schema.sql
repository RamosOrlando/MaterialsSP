drop extension if exists "pg_net";


  create table "public"."Category" (
    "categoryId" text not null,
    "name" text not null,
    "imagePath" text,
    "description" text default ''::text
      );



  create table "public"."Maker" (
    "makerId" text not null,
    "name" text not null,
    "imagePath" text
      );



  create table "public"."Material" (
    "materialId" text not null,
    "name" text not null,
    "unit" text not null,
    "makerId" text not null,
    "sectionId" text not null,
    "price" double precision,
    "quoteDate" text,
    "specId" text,
    "historyId" text,
    "providerId" text,
    constraint "material_pkey" primary key ("materialId")
  );



  create table "public"."Section" (
    "sectionId" text not null,
    "name" text not null,
    "categoryId" text not null,
    "imagePath" text
      );



create table public."PriceHistory" (
  "historyId" text not null,
  "materialId" text not null,
  "providerId" text not null,
  "price" double precision not null,
  "quoteDate" text not null,
  "username" text not null,
  constraint pricehistory_pkey primary key ("historyId"),
  constraint pricehistory_materialid_fkey foreign KEY ("materialId") references "Material" ("materialId"),
  constraint pricehistory_providerid_fkey foreign KEY ("providerId") references "Provider" ("providerId")
) TABLESPACE pg_default;


  create table public."Provider" (
    "providerId" text not null,
    "name" text not null,
    address text null,
    telephone bigint null,
    city text null,
    email text null,
    "imagePath" text null,
    constraint serv12_pkey primary key ("providerId"),
    constraint proce_name_descrip_unique unique (name, address)
  ) TABLESPACE pg_default;


CREATE UNIQUE INDEX category_name_key ON public."Category" USING btree (name);

CREATE UNIQUE INDEX category_pkey ON public."Category" USING btree ("categoryId");

CREATE UNIQUE INDEX maker_pkey1 ON public."Maker" USING btree ("makerId");



CREATE UNIQUE INDEX section_name_key ON public."Section" USING btree (name);

CREATE UNIQUE INDEX section_pkey ON public."Section" USING btree ("sectionId");

alter table "public"."Category" add constraint "category_pkey" PRIMARY KEY using index "category_pkey";

alter table "public"."Maker" add constraint "maker_pkey1" PRIMARY KEY using index "maker_pkey1";

alter table "public"."Section" add constraint "section_pkey" PRIMARY KEY using index "section_pkey";



alter table "public"."Category" add constraint "category_name_key" UNIQUE using index "category_name_key";


alter table "public"."Material" add constraint "material_makerId_fkey" FOREIGN KEY ("makerId") REFERENCES public."Maker"("makerId");

alter table "public"."Material" add constraint "material_sectionId_fkey" FOREIGN KEY ("sectionId") REFERENCES public."Section"("sectionId");

alter table "public"."Section" add constraint "section_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES public."Category"("categoryId") not valid;

alter table "public"."Section" validate constraint "section_categoryId_fkey";

alter table "public"."Section" add constraint "section_name_key" UNIQUE using index "section_name_key";


grant delete on table "public"."Category" to "anon";

grant insert on table "public"."Category" to "anon";

grant references on table "public"."Category" to "anon";

grant select on table "public"."Category" to "anon";

grant trigger on table "public"."Category" to "anon";

grant truncate on table "public"."Category" to "anon";

grant update on table "public"."Category" to "anon";

grant delete on table "public"."Category" to "authenticated";

grant insert on table "public"."Category" to "authenticated";

grant references on table "public"."Category" to "authenticated";

grant select on table "public"."Category" to "authenticated";

grant trigger on table "public"."Category" to "authenticated";

grant truncate on table "public"."Category" to "authenticated";

grant update on table "public"."Category" to "authenticated";

grant delete on table "public"."Category" to "service_role";

grant insert on table "public"."Category" to "service_role";

grant references on table "public"."Category" to "service_role";

grant select on table "public"."Category" to "service_role";

grant trigger on table "public"."Category" to "service_role";

grant truncate on table "public"."Category" to "service_role";

grant update on table "public"."Category" to "service_role";

grant delete on table "public"."Maker" to "anon";

grant insert on table "public"."Maker" to "anon";

grant references on table "public"."Maker" to "anon";

grant select on table "public"."Maker" to "anon";

grant trigger on table "public"."Maker" to "anon";

grant truncate on table "public"."Maker" to "anon";

grant update on table "public"."Maker" to "anon";

grant delete on table "public"."Maker" to "authenticated";

grant insert on table "public"."Maker" to "authenticated";

grant references on table "public"."Maker" to "authenticated";

grant select on table "public"."Maker" to "authenticated";

grant trigger on table "public"."Maker" to "authenticated";

grant truncate on table "public"."Maker" to "authenticated";

grant update on table "public"."Maker" to "authenticated";

grant delete on table "public"."Maker" to "service_role";

grant insert on table "public"."Maker" to "service_role";

grant references on table "public"."Maker" to "service_role";

grant select on table "public"."Maker" to "service_role";

grant trigger on table "public"."Maker" to "service_role";

grant truncate on table "public"."Maker" to "service_role";

grant update on table "public"."Maker" to "service_role";

grant delete on table "public"."Material" to "anon";

grant insert on table "public"."Material" to "anon";

grant references on table "public"."Material" to "anon";

grant select on table "public"."Material" to "anon";

grant trigger on table "public"."Material" to "anon";

grant truncate on table "public"."Material" to "anon";

grant update on table "public"."Material" to "anon";

grant delete on table "public"."Material" to "authenticated";

grant insert on table "public"."Material" to "authenticated";

grant references on table "public"."Material" to "authenticated";

grant select on table "public"."Material" to "authenticated";

grant trigger on table "public"."Material" to "authenticated";

grant truncate on table "public"."Material" to "authenticated";

grant update on table "public"."Material" to "authenticated";

grant delete on table "public"."Material" to "service_role";

grant insert on table "public"."Material" to "service_role";

grant references on table "public"."Material" to "service_role";

grant select on table "public"."Material" to "service_role";

grant trigger on table "public"."Material" to "service_role";

grant truncate on table "public"."Material" to "service_role";

grant update on table "public"."Material" to "service_role";

grant delete on table "public"."Section" to "anon";

grant insert on table "public"."Section" to "anon";

grant references on table "public"."Section" to "anon";

grant select on table "public"."Section" to "anon";

grant trigger on table "public"."Section" to "anon";

grant truncate on table "public"."Section" to "anon";

grant update on table "public"."Section" to "anon";

grant delete on table "public"."Section" to "authenticated";

grant insert on table "public"."Section" to "authenticated";

grant references on table "public"."Section" to "authenticated";

grant select on table "public"."Section" to "authenticated";

grant trigger on table "public"."Section" to "authenticated";

grant truncate on table "public"."Section" to "authenticated";

grant update on table "public"."Section" to "authenticated";

grant delete on table "public"."Section" to "service_role";

grant insert on table "public"."Section" to "service_role";

grant references on table "public"."Section" to "service_role";

grant select on table "public"."Section" to "service_role";

grant trigger on table "public"."Section" to "service_role";

grant truncate on table "public"."Section" to "service_role";

grant update on table "public"."Section" to "service_role";

grant delete on table "public"."PriceHistory" to "anon";

grant insert on table "public"."PriceHistory" to "anon";

grant references on table "public"."PriceHistory" to "anon";

grant select on table "public"."PriceHistory" to "anon";

grant trigger on table "public"."PriceHistory" to "anon";

grant truncate on table "public"."PriceHistory" to "anon";

grant update on table "public"."PriceHistory" to "anon";

grant delete on table "public"."PriceHistory" to "authenticated";

grant insert on table "public"."PriceHistory" to "authenticated";

grant references on table "public"."PriceHistory" to "authenticated";

grant select on table "public"."PriceHistory" to "authenticated";

grant trigger on table "public"."PriceHistory" to "authenticated";

grant truncate on table "public"."PriceHistory" to "authenticated";

grant update on table "public"."PriceHistory" to "authenticated";

grant delete on table "public"."PriceHistory" to "service_role";

grant insert on table "public"."PriceHistory" to "service_role";

grant references on table "public"."PriceHistory" to "service_role";

grant select on table "public"."PriceHistory" to "service_role";

grant trigger on table "public"."PriceHistory" to "service_role";

grant truncate on table "public"."PriceHistory" to "service_role";

grant update on table "public"."PriceHistory" to "service_role";

grant delete on table "public"."Provider" to "anon";

grant insert on table "public"."Provider" to "anon";

grant references on table "public"."Provider" to "anon";

grant select on table "public"."Provider" to "anon";

grant trigger on table "public"."Provider" to "anon";

grant truncate on table "public"."Provider" to "anon";

grant update on table "public"."Provider" to "anon";

grant delete on table "public"."Provider" to "authenticated";

grant insert on table "public"."Provider" to "authenticated";

grant references on table "public"."Provider" to "authenticated";

grant select on table "public"."Provider" to "authenticated";

grant trigger on table "public"."Provider" to "authenticated";

grant truncate on table "public"."Provider" to "authenticated";

grant update on table "public"."Provider" to "authenticated";

grant delete on table "public"."Provider" to "service_role";

grant insert on table "public"."Provider" to "service_role";

grant references on table "public"."Provider" to "service_role";

grant select on table "public"."Provider" to "service_role";

grant trigger on table "public"."Provider" to "service_role";

grant truncate on table "public"."Provider" to "service_role";

grant update on table "public"."Provider" to "service_role";

CREATE OR REPLACE FUNCTION public.set_material_current_price()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
  v_material_id text;
  v_history_id    public."PriceHistory"."historyId"%TYPE;
  v_provider_id    public."PriceHistory"."providerId"%TYPE;
  v_price       public."PriceHistory".price%TYPE;
  v_quoteDate   public."PriceHistory"."quoteDate"%TYPE;
BEGIN
  -- Para INSERT/UPDATE usamos NEW; para DELETE usamos OLD
  IF (TG_OP = 'DELETE') THEN
    v_material_id := OLD."materialId";
  ELSE
    v_material_id := NEW."materialId";
  END IF;

  -- Tomar el más reciente para ese MaterialId
  SELECT ph2."historyId", ph2."providerId", ph2.price, ph2."quoteDate"
  INTO v_history_id, v_provider_id, v_price, v_quoteDate
  FROM public."PriceHistory" ph2
  WHERE ph2."materialId" = v_material_id
  ORDER BY to_date(ph2."quoteDate", 'DD/MM/YYYY') DESC
  LIMIT 1;

  -- Si no existe ninguno => dejar NULL (en blanco)
  IF v_price IS NULL AND v_quoteDate IS NULL THEN
    UPDATE public."Material" m
    SET
      "price" = NULL,
      "quoteDate" = NULL,
      "historyId" = NULL,
      "providerId" = NULL
    WHERE m."materialId" = v_material_id;
  ELSE
    UPDATE public."Material" m
    SET
      "price" = v_price,
      "quoteDate" = v_quoteDate,
      "historyId" = v_history_id,
      "providerId" = v_provider_id
    WHERE m."materialId" = v_material_id;
  END IF;

  -- Para DELETE, el return debe ser OLD; para demás ops, NEW
  IF (TG_OP = 'DELETE') THEN
    RETURN OLD;
  ELSE
    RETURN NEW;
  END IF;
END;
$function$;

create trigger trg_set_material_current_price_del
after DELETE on "PriceHistory" for EACH row
execute FUNCTION set_material_current_price ();

create trigger trg_set_material_current_price_insupd
after INSERT
or
update OF price,
"quoteDate" on "PriceHistory" for EACH row
execute FUNCTION set_material_current_price ();


