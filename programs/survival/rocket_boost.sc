// Rocket Boost for Scarpet by MeeniMc
// LICENSE: CC-BY-NC-SA

// Fire a rocket while looking in the air to use it as propellant
// If you have Elytras on, you will start flying
// If you don't have Elytras, you will run fast for a while

// It doesn't work underwater; if you want it to, remove 'liquids' from the 'trace' query

//   Requires Scarpet 1.7 from carpet mod 1.4.15+, both from the amazing gnembon (although...how'd you get this far without knowing that?); https://github.com/gnembon/fabric-carpet/releases

__config() ->
(
  m(
    l('stay_loaded', true),
    l('scope', 'global')
  )
);

__on_player_uses_item(player, item_tuple, hand) ->
(
  if( hand != 'mainhand'
  &&  hand != 'offhand'
  || query(player, 'pose')~'fall_flying' // No need to overload normal elytra behavior.
  || query(player, 'is_riding'), // Don't fly out of vehicles.
    return()
  );

  l(item,count,nbt) = item_tuple || l('None', 0, null);
  tgt = query(player, 'trace', 4.5, 'liquids');
  print('player='+player+' hand='+hand+'('+player~'selected_slot'+') item='+item+'('+count+') target='+tgt);

  if( null == tgt
  &&  item == 'firework_rocket',

    // set ourselves in Elytra mode if we have one equipped.
    chest = query(player, 'holds', 'chest');
    print('chest='+chest);
    if( 'elytra' == chest:0,
    //||  get(chest, 'colytra:ElytraUpgrade':'damage')<431,
    modify(player, 'move', 0, 0.5, 0);
    //TODO: set player in elytra mode, don't know how yet.


    if( !player~'gamemode_id'%2, // use a rocket from the hand, only if not in creative
      print('decrease '+item+' in '+hand+'('+player~'selected_slot'+') to '+(count-1));
      //inventory_set(player,  player~'selected_slot', count-1);
    );


    // Each firework also determines its lifetime in ticks by 10 × (number of gunpowder + 1) + random value from 0 to 5 + random value from 0 to 6
    //   https://minecraft.gamepedia.com/Firework_Rocket#Elytra
    duration = nbt:'Fireworks':'Flight' || 1;
    duration = 10*(duration+1)+floor(rand(5))+floor(rand(6));
//    schedule(0, '_rocket_boost', player, duration);
    //TODO: use the rocket item as-normal now that we are in elytra mode.
  )
);

_rocket_boost(player, duration) ->
(
  vector = 1.693*query(player, 'look'); //speed boost to 33.86 m/s
  print('motion vector '+vector+' remains '+duration+' pose '+query(player, 'pose'));
  modify(player, 'motion', vector);
  );
  duration+=-1;
  if( 0 < duration,
    schedule(1, '_rocket_boost', player, duration)
  )
);
