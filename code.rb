class WebDataIndegration
  ALIAS = ["ns:base.wikipedia_infobox.video_game.platforms"]
  OpenSSL::SSL::VERIFY_PEER = OpenSSL::SSL::VERIFY_NONE

  def get_from_giantbomb
    resource_ids = (1000..99999).to_a
    f = File.new("giantbomb_0.json", "a")
    resource_ids.each do |resource_id|
      link = "http://www.giantbomb.com/api/game/3030-"+resource_id.to_s+"/?api_key=76b2a205a21b85b9dc004dbe9cae59aadcf14c9b&format=json"
      uri = URI(link)
      s = Net::HTTP.get(uri)
      s.encode!('UTF-8', {:invalid => :replace, :undef => :replace, :replace => '?'})
      j = JSON.parse(s)
      if j["error"] == "OK"
        f.write(j["results"])
        p resource_id
      elsif j["error"] == "Object Not Found"
        p "404 " + resource_id.to_s
        next
      elsif j["error"].start_with?("Rate limit")
        p "Waiting"
        sleep(60*10)
        next
      else
        f.close
        p j["error"]
        return
      end
    end
    f.close
  end

  def get_from_freebase_by_mid(mid)
    uri = URI.parse("https://www.googleapis.com/freebase/v1/rdf/" << mid)
    http = Net::HTTP.new(uri.host, uri.port)
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_PEER
    request = Net::HTTP::Get.new(uri.request_uri)
    response = http.request(request)
    if response.code == "403"
      response.code  
    else
      response.body
    end
  end

  def get_games_from_freebase_dump
    res = File.new("freebase.rdf", "wb+")
    gz_path = "c:/Users/Denis/AppData/Local/Temp/freebase-rdf-2014-10-05-00-00.gz"
    gz_file = File.open(gz_path, "rb") do |f|
      gz = Zlib::GzipReader.new(f)
      gz.each_line do |line|
        line.encode!('UTF-8', {:invalid => :replace, :undef => :replace, :replace => '?'})
        if line.include?("cvg.computer_videogame")
          res.write(line)
        end
      end
      gz.close
    end
    res.close
  end

  def first_level_download_freebase
    input = File.open("freebase.rdf", "r")
    output = File.new("freebase_out.rdf", "w+")
    content = input.read
    addr = []
    content.each_line do |line|
      str = line.split("\t")[0]
      url = str[1..-2] 
      addr << url
    end
    addr = addr.uniq
    addr.each do |address| 
      mid = address[27..-1].sub!(".", "/")  
      r = get_from_freebase_by_mid(mid)
      p mid
      flag = 0
      if r == "403" && flag == 0
        p "sleeping"
        sleep(5*60)
        flag += 1
        redo
      elsif r == "403" && flag != 0
        next
      elsif r != "403"
        output.write(r)
      end
    end
    output.close
  end

  def second_level_download_freebase
    f = File.open("freebase_out.rdf", "r")
    f.each_line do |line|
    ALIAS.each do |allias|
      if line.strip.start_with?(allias)
        p line
        index = line.index("m.")
        mid_2 = line[index..-3].sub(".", "/")
        rd = get_content(mid_2)
        if rd == "403"
          p "sleeping"
          sleep(5*60)
          redo
        else
          m = File.new("mids/"<<mid_2.sub("/", "."), "w+")
          m.write(rd)
          m.close
        end
      end
    end
    f.close
  end

  def merge_freebase
    main = File.open("freebase_out.rdf", "r")
    dir = Dir.entries("mids")
    res = File.open("free_res.rdf", "w")
    main.each_line do |line|
      tmp = line
      dir.each do |mid|
        unless mid == "." || mid == ".."
          p mid
          mid_file = File.open("hope_1/"<<mid, "r")
          mid_content = mid_file.read
          tmp = line.gsub(mid, mid_content)
          mid_file.close
        end
      end
      res.write(tmp) 
    end
    main.close
    res.close
  end

  def prepare_for_identity_resolution
    files = ["giantbomb.xml", "dbpedia.xml", "thegamesdb.xml", "freebase.xml"]
    files.each do |file|
    input = File.open("XML/#{file}", "r")
    output = File.open("XML_out/#{file}", "w")
    del = [" a ", " the ", ",", ".", ":", "-"]
    input.each_line do |line|
      line.downcase!
      del.each { |sym| line.gsub!(sym, "") } if line.include?("<title>")
      p file
      output.write(line)
    end
    input.close
    output.close
  end
  
  def intersection(f1,f2)
    a = File.open(f1, "r").read.split("|")
    b = File.open(f2, "r").read.split("|")
    a_b = (a - b).uniq
    b_a = (b - a).uniq
    ab = (a + b).uniq
    ba = (b_a + a_b).uniq
    (ab-ba).uniq
  end

  def get_title_and_id_to_csv(file)
    ids = []
    titles = []
    input = File.open("XML_out/#{file}", "r")
    input.each_line do |line|
      titles << line if line.include?("<title>")
      ids << line if line.include?("<id>")
      p "+"
    end
    input.close
    output_t = File.new("XML_out/#{file}_titles.cvs", "w")
    output_t.write(titles.join("|"))
    output_i = File.new("XML_out/#{file}_ids.csv", "w")
    output_i.write(ids.join("|"))
    output_i.close
    output_t.close
  end

end