-- ──────────────────────────────────────────────
-- Seed admin user (used as created_by for events)
-- ──────────────────────────────────────────────
insert into users (first_name, last_name, email, password_hash, role, created_at)
values ('Seed', 'Admin', 'seedadmin@ticketmonster.com', 'NOT_A_REAL_HASH', 'ADMIN', current_timestamp)
on conflict (email) do nothing;

-- ──────────────────────────────────────────────
-- Categories
-- ──────────────────────────────────────────────
insert into categories (category_name) values
    ('Concert'),
    ('Sports'),
    ('Arts & Theatre'),
    ('Comedy'),
    ('Family'),
    ('Travel')
on conflict (category_name) do nothing;

-- ──────────────────────────────────────────────
-- Venues
-- ──────────────────────────────────────────────
insert into venues (venue_name, address, city, capacity) values
    ('Bell Centre',             '1909 Ave des Canadiens-de-Montréal',   'Montreal',     21302),
    ('Place des Arts',          '175 Rue Sainte-Catherine O',           'Montreal',     2982),
    ('Molson Stadium',          '475 Pine Ave W',                       'Montreal',     25000),
    ('MTelus',                  '59 Rue Sainte-Catherine E',            'Montreal',     2300),
    ('Olympia de Montréal',     '1004 Rue Sainte-Catherine E',          'Montreal',     2000),
    ('Videotron Centre',        '250 Rue de la Rocade',                 'Quebec City',  18259),
    ('Scotiabank Arena',        '40 Bay St',                            'Toronto',      19800),
    ('Rogers Centre',           '1 Blue Jays Way',                      'Toronto',      53506);

-- ──────────────────────────────────────────────
-- Events
-- ──────────────────────────────────────────────
do $$
declare
    v_admin_id      int;
    v_bell          int;
    v_place_arts    int;
    v_molson        int;
    v_mtelus        int;
    v_olympia       int;
    v_videotron     int;
    v_scotiabank    int;
    v_rogers        int;

    v_cat_concert   int;
    v_cat_sports    int;
    v_cat_arts      int;
    v_cat_comedy    int;
    v_cat_family    int;
    v_cat_travel    int;
begin
    select user_id     into v_admin_id    from users      where email         = 'seedadmin@ticketmonster.com';

    select venue_id    into v_bell        from venues     where venue_name    = 'Bell Centre';
    select venue_id    into v_place_arts  from venues     where venue_name    = 'Place des Arts';
    select venue_id    into v_molson      from venues     where venue_name    = 'Molson Stadium';
    select venue_id    into v_mtelus      from venues     where venue_name    = 'MTelus';
    select venue_id    into v_olympia     from venues     where venue_name    = 'Olympia de Montréal';
    select venue_id    into v_videotron   from venues     where venue_name    = 'Videotron Centre';
    select venue_id    into v_scotiabank  from venues     where venue_name    = 'Scotiabank Arena';
    select venue_id    into v_rogers      from venues     where venue_name    = 'Rogers Centre';

    select category_id into v_cat_concert from categories where category_name = 'Concert';
    select category_id into v_cat_sports  from categories where category_name = 'Sports';
    select category_id into v_cat_arts    from categories where category_name = 'Arts & Theatre';
    select category_id into v_cat_comedy  from categories where category_name = 'Comedy';
    select category_id into v_cat_family  from categories where category_name = 'Family';
    select category_id into v_cat_travel  from categories where category_name = 'Travel';

    insert into events (title, description, event_date, available_tickets, price, status, venue_id, category_id, created_by) values

    -- Concerts
    (
        'Taylor Swift – The Eras Tour',
        'The record-breaking Eras Tour returns for one final Montreal night. Spanning every era of Taylor''s catalogue, this is three hours of pure magic.',
        '2026-04-15 19:30:00', 18000, 189.99, 'ACTIVE', v_bell, v_cat_concert, v_admin_id
    ),
    (
        'The Weeknd – After Hours Til Dawn',
        'Abel Tesfaye brings his cinematic world tour to MTelus for an intimate performance night packed with chart-topping hits.',
        '2026-05-03 20:00:00', 2100, 149.99, 'ACTIVE', v_mtelus, v_cat_concert, v_admin_id
    ),
    (
        'Billie Eilish – Hit Me Hard and Soft Tour',
        'Grammy-winning artist Billie Eilish performs tracks from her critically acclaimed album in a breathtaking visual production.',
        '2026-05-20 19:00:00', 17500, 129.99, 'ACTIVE', v_bell, v_cat_concert, v_admin_id
    ),
    (
        'Drake & 21 Savage – It''s All a Blur',
        'Two of hip-hop''s biggest names share the stage in a night of their greatest hits and brand-new material.',
        '2026-06-05 20:30:00', 20000, 165.00, 'ACTIVE', v_bell, v_cat_concert, v_admin_id
    ),
    (
        'Kendrick Lamar – Grand National Tour',
        'Fresh off his record-breaking Super Bowl halftime show, Pulitzer Prize-winning rapper Kendrick Lamar brings his Grand National Tour to Montreal.',
        '2026-06-28 20:00:00', 19000, 175.00, 'ACTIVE', v_bell, v_cat_concert, v_admin_id
    ),
    (
        'Sabrina Carpenter – Short n'' Sweet Tour',
        'Pop sensation Sabrina Carpenter performs her platinum album live, bringing the catchy hooks and stunning visuals of Short n'' Sweet to the stage.',
        '2026-07-12 19:30:00', 2200, 99.99, 'ACTIVE', v_mtelus, v_cat_concert, v_admin_id
    ),
    (
        'Coldplay – Music of the Spheres',
        'Coldplay''s spectacular world tour arrives in Montreal with its iconic light shows, sustainable confetti, and decades of anthems.',
        '2026-08-02 20:00:00', 21000, 195.00, 'ACTIVE', v_bell, v_cat_concert, v_admin_id
    ),

    -- Sports
    (
        'Montreal Canadiens vs Toronto Maple Leafs',
        'The Battle of Quebec''s rivals heats up as the Habs host the Leafs in a must-watch regular-season clash at the Bell Centre.',
        '2026-04-08 19:00:00', 9000, 85.00, 'ACTIVE', v_bell, v_cat_sports, v_admin_id
    ),
    (
        'Montreal Alouettes vs Toronto Argonauts',
        'CFL action returns to Molson Stadium as the Alouettes take on the defending champion Toronto Argonauts under the lights.',
        '2026-06-12 19:30:00', 22000, 45.00, 'ACTIVE', v_molson, v_cat_sports, v_admin_id
    ),
    (
        'Toronto Blue Jays vs Boston Red Sox',
        'AL East rivals clash at Rogers Centre in a pivotal three-game series with playoff implications on the line.',
        '2026-07-18 13:07:00', 40000, 35.00, 'ACTIVE', v_rogers, v_cat_sports, v_admin_id
    ),
    (
        'UFC 315 – Montreal Fight Night',
        'The octagon returns to Montreal for a stacked UFC card headlined by a title fight between two elite middleweights.',
        '2026-05-10 18:00:00', 18000, 120.00, 'ACTIVE', v_bell, v_cat_sports, v_admin_id
    ),

    -- Arts & Theatre
    (
        'Hamilton – The Musical',
        'Lin-Manuel Miranda''s groundbreaking musical about the life of Alexander Hamilton comes to Place des Arts for a limited run.',
        '2026-04-22 19:30:00', 2500, 110.00, 'ACTIVE', v_place_arts, v_cat_arts, v_admin_id
    ),
    (
        'Cirque du Soleil – Alegría',
        'The beloved Cirque du Soleil classic Alegría returns in a stunning reimagined production filled with acrobatics, music, and spectacle.',
        '2026-05-15 20:00:00', 2800, 95.00, 'ACTIVE', v_place_arts, v_cat_arts, v_admin_id
    ),
    (
        'The Phantom of the Opera',
        'Andrew Lloyd Webber''s timeless masterpiece returns to Montreal with a brand-new touring cast and fully restored sets.',
        '2026-07-08 19:30:00', 2700, 105.00, 'ACTIVE', v_place_arts, v_cat_arts, v_admin_id
    ),

    -- Comedy
    (
        'Kevin Hart – Reality Check Tour',
        'Comedy superstar Kevin Hart brings his side-splitting Reality Check Tour to Montreal for one unforgettable night.',
        '2026-05-28 20:00:00', 2000, 89.99, 'ACTIVE', v_mtelus, v_cat_comedy, v_admin_id
    ),
    (
        'Dave Chappelle – Live Stand-Up',
        'One of the greatest stand-up comedians of all time takes the stage at the Olympia for an unfiltered and hilarious evening.',
        '2026-06-18 21:00:00', 1900, 95.00, 'ACTIVE', v_olympia, v_cat_comedy, v_admin_id
    ),
    (
        'Howie Mandel – Surrounded',
        'TV host and comedian Howie Mandel performs his new stand-up set filled with stories, observations, and unexpected twists.',
        '2026-07-25 20:00:00', 1800, 69.99, 'ACTIVE', v_olympia, v_cat_comedy, v_admin_id
    ),

    -- Family
    (
        'Disney On Ice – Dream Big',
        'Mickey, Minnie, and all your favourite Disney characters glide across the ice in this magical family spectacular.',
        '2026-04-05 14:00:00', 15000, 55.00, 'ACTIVE', v_bell, v_cat_family, v_admin_id
    ),
    (
        'Harlem Globetrotters',
        'The world-famous Harlem Globetrotters bring their jaw-dropping ball-handling skills and family-friendly entertainment to Quebec City.',
        '2026-04-19 15:00:00', 12000, 40.00, 'ACTIVE', v_videotron, v_cat_family, v_admin_id
    ),
    (
        'PAW Patrol Live! – The Great Pirate Adventure',
        'Chase, Marshall, Skye, and the rest of the PAW Patrol crew star in this exciting live stage show perfect for young fans.',
        '2026-05-09 11:00:00', 10000, 35.00, 'ACTIVE', v_videotron, v_cat_family, v_admin_id
    );

end $$;
