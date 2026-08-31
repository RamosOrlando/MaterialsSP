-- Allow anonymous access to metadata tables for registration
grant select on table "public"."UserRole" to "anon", "authenticated";
grant select on table "public"."UserProfession" to "anon", "authenticated";
grant select on table "public"."UserPlan" to "anon", "authenticated";

-- Also allow authenticated users to read their own profile and history
grant select, insert, update on table "public"."User" to "authenticated";
grant select, insert on table "public"."SubscriptionHistory" to "authenticated";

-- Enable RLS for security
alter table "public"."User" enable row level security;
alter table "public"."SubscriptionHistory" enable row level security;

-- Policies for User table
create policy "Users can view their own profile" on "public"."User"
  for select using (auth.uid()::text = "userId");

create policy "Users can update their own profile" on "public"."User"
  for update using (auth.uid()::text = "userId");

create policy "Users can insert their own profile" on "public"."User"
  for insert with check (auth.uid()::text = "userId");

-- Policies for SubscriptionHistory
create policy "Users can view their own subscription history" on "public"."SubscriptionHistory"
  for select using (auth.uid()::text = "userId");
