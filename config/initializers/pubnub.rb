$pubnub = Pubnub.new(
    :publish_key   => "pubkey",
    :subscribe_key => "subkey"
)

$callback = lambda do |envelope|
  Message.create(
      :author => envelope.msg['author'],
      :message => envelope.msg['message'],
      :timetoken => envelope.timetoken
  ) if envelope.msg['author'] && envelope.msg['message']
end
