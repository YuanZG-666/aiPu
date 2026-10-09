<template>
  <div class="bead-studio">
    <header class="topbar">
      <div class="brand">
        <div class="brand-icon"><i v-for="n in 9" :key="n"></i></div>
        <div><b>豆豆工坊 <small>BEAD STUDIO</small></b><span>把喜欢的画面，拼成手心里的像素</span></div>
      </div>
      <button class="guide-button" @click="guideOpen = !guideOpen"><i class="el-icon-question"></i> 使用指南</button>
    </header>

    <div v-if="guideOpen" class="guide">
      <span><b>01</b> 上传图片</span><i>→</i><span><b>02</b> 调整豆数和色数</span><i>→</i><span><b>03</b> 下载图纸开始拼</span>
      <button @click="guideOpen = false">×</button>
    </div>

    <section class="hero">
      <div class="hero-copy">
        <div class="eyebrow"><i></i> PIXEL TO BEADS · 01</div>
        <h1>让灵感，<em>落成豆子。</em></h1>
        <p>上传像素图或照片，自动匹配 Hama MIDI 实体色号，生成可以直接照着拼的网格图纸。</p>
        <div class="hero-points"><span><i class="el-icon-check"></i> 色号自动匹配</span><span><i
            class="el-icon-check"></i> 豆数清单同步生成</span><span><i class="el-icon-check"></i> 图片在本地处理</span>
        </div>
      </div>
      <div class="hero-art">
        <div class="art-spark">✳</div>
        <div class="art-board"><i v-for="(color, i) in heroBeads" :key="i" :style="{background:color}"></i></div>
        <div class="art-note">MAKE IT<br>PIXEL PERFECT</div>
      </div>
    </section>

    <main class="workbench">
      <aside class="settings-column">
        <section class="panel settings-card">
          <div class="panel-title">
            <div><small>STEP 01</small>
              <h2>放入你的图片</h2></div>
            <i class="el-icon-picture-outline title-icon"></i></div>
          <input ref="fileInput" class="hidden-input" type="file" accept="image/png,image/jpeg,image/webp,image/gif"
                 @change="onFileChange">
          <div class="dropzone" :class="{dragging:isDragging, 'has-image':sourceImage}" @click="triggerFilePicker"
               @dragover.prevent="isDragging=true" @dragleave.prevent="isDragging=false" @drop.prevent="onDrop">
            <template v-if="sourceImage"><img :src="sourceUrl" alt="图片预览"><b>点击更换图片</b><small>{{ fileName }} ·
              {{ imageDimensions }}</small><i class="el-icon-refresh replace-icon"></i></template>
            <template v-else><i class="el-icon-upload2 upload-icon"></i><b>拖一张图片到这里</b><small>或点击选择 · PNG /
              JPG / WebP</small></template>
          </div>
          <button v-if="!sourceImage" class="sample-link" @click="loadSample"><i class="el-icon-magic-stick"></i>
            先看一张示例图纸 <span>→</span></button>
          <div v-else class="privacy-hint"><i class="el-icon-lock"></i> 图片只在当前浏览器中处理，不会上传服务器</div>
        </section>

        <section class="panel settings-card">
          <div class="panel-title">
            <div><small>STEP 02</small>
              <h2>调整拼豆规格</h2></div>
            <i class="el-icon-set-up title-icon mint"></i></div>
          <div class="palette-card"><b
              class="palette-mark">H</b><span><strong>Hama MIDI</strong><small>常用实色 · 官方色号</small></span><em>{{
              palette.length
            }} 色</em></div>
          <a class="palette-link" href="https://hama.dk/en/pages/colour-chart" target="_blank"
             rel="noopener noreferrer">查看 Hama 官方色卡 <i class="el-icon-top-right"></i></a>

          <div class="range-setting">
            <div><span>图纸宽度</span><b>{{ gridWidth }} <small>颗</small></b></div>
            <input v-model.number="gridWidth" type="range" min="12" max="64" :disabled="isSample"
                   @change="rebuildPattern">
            <footer><span>细节更少</span><span>细节更多</span></footer>
          </div>
          <div class="range-setting">
            <div><span>最多使用颜色</span><b>{{ maxColors }} <small>色</small></b></div>
            <input v-model.number="maxColors" type="range" min="8" :max="palette.length" :disabled="isSample"
                   @change="rebuildPattern">
            <footer><span>更简洁</span><span>更接近原图</span></footer>
          </div>
          <div v-if="isSample" class="control-hint"><i class="el-icon-info"></i> 上传图片后可自定义尺寸和色数</div>
          <div class="switch-row"><span><b>格子内显示色号</b><small>方便逐颗对照摆放</small></span>
            <button class="toggle" :class="{active:showCodes}" role="switch" :aria-checked="showCodes"
                    @click="toggleCodes"><i></i></button>
          </div>
          <div class="switch-row last-switch"><span><b>显示网格与坐标</b><small>每 5 格标注一次行列</small></span>
            <button class="toggle" :class="{active:showGrid}" role="switch" :aria-checked="showGrid"
                    @click="toggleGrid"><i></i></button>
          </div>
        </section>
        <div class="privacy-note"><i class="el-icon-lock"></i> 图像处理完全在本地浏览器运行，原图不会离开你的设备。</div>
      </aside>

      <section class="panel pattern-panel">
        <div class="pattern-heading">
          <div><small>STEP 03 · YOUR PATTERN</small>
            <h2>拼豆图纸 <em v-if="isSample">示例</em></h2>
            <div class="pattern-meta"><span><i class="el-icon-menu"></i> {{ columns }} × {{ rows }} 颗</span><span><i
                class="el-icon-collection-tag"></i> {{ usedColors.length }} 种颜色</span><span><i
                class="el-icon-sunny"></i> 约 {{ formatNumber(totalBeads) }} 颗豆子</span></div>
          </div>
          <div class="actions">
            <button class="print-button" :disabled="!beadGrid.length" @click="printPattern"><i
                class="el-icon-printer"></i><span>打印 / PDF</span></button>
            <button class="download-button" :disabled="!beadGrid.length" @click="downloadPattern"><i
                class="el-icon-download"></i><span>下载 PNG</span></button>
          </div>
        </div>
        <div class="canvas-toolbar"><span><i class="live-dot"></i> 图纸预览</span>
          <div class="zoom">
            <button aria-label="缩小预览" @click="zoom=Math.max(85,zoom-10)">−</button>
            <span>{{ zoom }}%</span>
            <button aria-label="放大预览" @click="zoom=Math.min(150,zoom+10)">＋</button>
          </div>
        </div>
        <div v-if="beadGrid.length" class="canvas-viewport">
          <canvas ref="boardCanvas" class="board-canvas" :style="{width:zoom+'%'}"></canvas>
        </div>
        <div v-else class="empty-board"><i class="el-icon-picture-outline"></i><b>图纸会在这里出现</b><span>上传图片，看看它变成拼豆图案的样子</span>
          <button @click="triggerFilePicker">选择一张图片 <i class="el-icon-right"></i></button>
        </div>
        <div class="board-footer"><span><i
            class="el-icon-info"></i> 屏幕色块为近似显示，实物请以 Hama 色卡为准。</span><span>坐标单位：颗</span></div>
      </section>
    </main>

    <section class="panel inventory-panel">
      <div class="inventory-heading">
        <div><small>COLOR BREAKDOWN</small>
          <h2>颜色与用量 <em>{{ usedColors.length }} 种</em></h2></div>
        <p>按颜色统计每种拼豆需要的数量</p></div>
      <div v-if="usedColors.length" class="color-list">
        <div v-for="color in usedColors" :key="color.code" class="color-item">
          <div class="swatch" :style="{background:color.hex}"><span v-if="isLight(color.hex)">{{ color.code }}</span>
          </div>
          <div class="color-name"><b>{{ color.code }} <span>{{ color.name }}</span></b><small>{{ color.english }} ·
            #{{ color.hex.slice(1) }}</small></div>
          <strong class="quantity">{{ formatNumber(colorCounts[color.code]) }}<small>颗</small></strong>
          <div class="quantity-track"><i
              :style="{width:quantityPercent(colorCounts[color.code])+'%',background:color.hex}"></i></div>
        </div>
      </div>
      <div v-else class="inventory-empty"><i class="el-icon-sugar"></i> 生成图纸后，这里会列出每种颜色的豆子用量。</div>
      <div v-if="usedColors.length" class="inventory-footer"><span>色号以 Hama MIDI 官方编号为基准</span><b>合计
        <strong>{{ formatNumber(totalBeads) }}</strong> 颗</b></div>
    </section>
    <footer class="studio-footer">
      <span>MADE FOR SLOW & HAPPY MAKING</span><span>豆豆工坊 <i>·</i> 让每一颗都恰到好处</span></footer>
  </div>
</template>

<script>
const HAMA_PALETTE = [
  {code: '01', name: '白色', english: 'White', hex: '#F4F2EB'}, {
    code: '02',
    name: '奶油色',
    english: 'Cream',
    hex: '#F0E0B7'
  },
  {code: '03', name: '黄色', english: 'Yellow', hex: '#EFCB35'}, {
    code: '04',
    name: '橙色',
    english: 'Orange',
    hex: '#E98435'
  },
  {code: '05', name: '红色', english: 'Red', hex: '#D94D4D'}, {
    code: '06',
    name: '粉色',
    english: 'Pink',
    hex: '#E883A1'
  },
  {code: '07', name: '紫色', english: 'Purple', hex: '#79539E'}, {
    code: '08',
    name: '蓝色',
    english: 'Blue',
    hex: '#3679B9'
  },
  {code: '09', name: '浅蓝色', english: 'Light blue', hex: '#80C2DD'}, {
    code: '10',
    name: '绿色',
    english: 'Green',
    hex: '#54945C'
  },
  {code: '11', name: '浅绿色', english: 'Light green', hex: '#8DBD59'}, {
    code: '12',
    name: '棕色',
    english: 'Brown',
    hex: '#82583E'
  },
  {code: '17', name: '灰色', english: 'Grey', hex: '#8C9294'}, {
    code: '18',
    name: '黑色',
    english: 'Black',
    hex: '#303237'
  },
  {code: '20', name: '红棕色', english: 'Reddish brown', hex: '#A65F46'}, {
    code: '21',
    name: '浅棕色',
    english: 'Light brown',
    hex: '#C69561'
  },
  {code: '22', name: '深红色', english: 'Dark red', hex: '#883B4A'}, {
    code: '26',
    name: '雾粉色',
    english: 'Blush',
    hex: '#D8919F'
  },
  {code: '27', name: '米色', english: 'Beige', hex: '#D6BE95'}, {
    code: '28',
    name: '深绿色',
    english: 'Dark green',
    hex: '#376647'
  },
  {code: '29', name: '酒红色', english: 'Claret', hex: '#81445C'}, {
    code: '30',
    name: '勃艮第红',
    english: 'Burgundy',
    hex: '#702D3F'
  },
  {code: '31', name: '青绿色', english: 'Turquoise', hex: '#42A9B7'}, {
    code: '32',
    name: '霓虹粉',
    english: 'Neon pink',
    hex: '#ED55A6'
  },
  {code: '33', name: '樱桃红', english: 'Cerise', hex: '#B94468'}, {
    code: '34',
    name: '霓虹黄',
    english: 'Neon yellow',
    hex: '#E8E84A'
  },
  {code: '35', name: '霓虹红', english: 'Neon red', hex: '#EE5B59'}, {
    code: '36',
    name: '霓虹蓝',
    english: 'Neon blue',
    hex: '#3973D4'
  },
  {code: '37', name: '霓虹绿', english: 'Neon green', hex: '#76C953'}, {
    code: '38',
    name: '霓虹橙',
    english: 'Neon orange',
    hex: '#F27C42'
  },
  {code: '42', name: '荧光绿', english: 'Fluorescent green', hex: '#A8CF5B'}, {
    code: '43',
    name: '浅黄色',
    english: 'Pastel yellow',
    hex: '#F1E68B'
  },
  {code: '44', name: '浅红色', english: 'Pastel red', hex: '#EBA6A9'}, {
    code: '45',
    name: '浅紫色',
    english: 'Pastel purple',
    hex: '#BDA7D2'
  },
  {code: '46', name: '浅蓝色', english: 'Pastel blue', hex: '#A3C8E5'}, {
    code: '47',
    name: '浅绿色',
    english: 'Pastel green',
    hex: '#A9D1A6'
  },
  {code: '48', name: '浅粉色', english: 'Pastel pink', hex: '#E8BBC7'}, {
    code: '49',
    name: '天蓝色',
    english: 'Azure',
    hex: '#258CBC'
  },
  {code: '60', name: '泰迪棕', english: 'Teddybear brown', hex: '#B47E59'}, {
    code: '70',
    name: '浅灰色',
    english: 'Light grey',
    hex: '#C5C7C8'
  },
  {code: '71', name: '深灰色', english: 'Dark grey', hex: '#5D6061'}, {
    code: '75',
    name: '浅牛轧糖色',
    english: 'Light nougat',
    hex: '#D1A77E'
  },
  {code: '76', name: '牛轧糖色', english: 'Nougat', hex: '#A77B59'}, {
    code: '77',
    name: '云白色',
    english: 'Cloudy white',
    hex: '#E6E2D7'
  },
  {code: '78', name: '浅桃色', english: 'Light peach', hex: '#F0B297'}, {
    code: '79',
    name: '杏色',
    english: 'Apricot',
    hex: '#E9936E'
  },
  {code: '82', name: '梅子色', english: 'Plum', hex: '#774468'}, {
    code: '83',
    name: '石油蓝',
    english: 'Petrol blue',
    hex: '#3D6D7C'
  },
  {code: '84', name: '橄榄绿', english: 'Olive green', hex: '#74784D'}, {
    code: '95',
    name: '浅粉色',
    english: 'Pastel rose',
    hex: '#EFC3CC'
  },
  {code: '96', name: '浅丁香紫', english: 'Pastel lilac', hex: '#CEC5E0'}, {
    code: '97',
    name: '冰蓝色',
    english: 'Pastel ice blue',
    hex: '#C4DDE8'
  },
  {code: '98', name: '薄荷绿', english: 'Pastel mint', hex: '#C3DED4'}, {
    code: '101',
    name: '桉树绿',
    english: 'Eucalyptus',
    hex: '#79A99B'
  },
  {code: '102', name: '森林绿', english: 'Forest green', hex: '#37634F'}
]
const DEMO = ['........PP........', '......PPPPPP......', '.....PPYYYYPP.....', '....PPYYYYYYPP....', '...PPYYYYYYYYPP...', '...PPYYYYYYYYPP...', '....PPYYYYYYPP....', '.....PPYYYYPP.....', '......PPPPPP......', '........PP........', '.........G........', '.........G........', '......GGGGGG......', '.......GGGG.......', '......GG..GG......', '........GG........', '..................', '..................']

function rgb(hex) {
  const s = hex.slice(1);
  return {r: parseInt(s.slice(0, 2), 16), g: parseInt(s.slice(2, 4), 16), b: parseInt(s.slice(4, 6), 16)}
}

function distance(a, b) {
  if (!a || !b) {
    console.warn('distance 参数为空！', a, b)
    return 999999;
  }
  const mr = (a.r + b.r) / 2, dr = a.r - b.r, dg = a.g - b.g, db = a.b - b.b;
  return (2 + mr / 256) * dr * dr + 4 * dg * dg + (2 + (255 - mr) / 256) * db * db
}

function demoGrid() {
  return DEMO.map(row => Array.from({length: 18}, (_, i) => row[i] === 'P' ? '48' : row[i] === 'Y' ? '43' : row[i] === 'G' ? '10' : null))
}

export default {
  name: 'Home',
  data() {
    const grid = demoGrid(), counts = {'48': 0, '43': 0, '10': 0}
    grid.forEach(row => row.forEach(code => {
      if (code) counts[code]++
    }))
    return {
      palette: HAMA_PALETTE,
      gridWidth: 18,
      maxColors: 12,
      showCodes: true,
      showGrid: true,
      zoom: 100,
      guideOpen: false,
      isDragging: false,
      isSample: true,
      fileName: '春日小花 · 示例',
      sourceImage: null,
      sourceUrl: '',
      columns: 18,
      rows: 18,
      beadGrid: grid,
      colorCounts: counts
    }
  },
  computed: {
    totalBeads() {
      return Object.keys(this.colorCounts).reduce((sum, code) => sum + this.colorCounts[code], 0)
    },
    usedColors() {
      return this.palette.filter(c => this.colorCounts[c.code] > 0).sort((a, b) => this.colorCounts[b.code] - this.colorCounts[a.code])
    },
    imageDimensions() {
      return this.sourceImage ? this.sourceImage.naturalWidth + ' × ' + this.sourceImage.naturalHeight + ' px' : ''
    },
    heroBeads() {
      return ['#E883A1', '#F0E0B7', '#EFCB35', '#54945C', '#80C2DD', '#79539E', '#D94D4D', '#F4F2EB', '#8DBD59']
    }
  },
  mounted() {
    this.drawBoard()
  },
  beforeDestroy() {
    if (this.sourceUrl) URL.revokeObjectURL(this.sourceUrl)
  },
  methods: {
    triggerFilePicker() {
      if (this.$refs.fileInput) this.$refs.fileInput.click()
    },
    onFileChange(e) {
      const file = e.target.files && e.target.files[0];
      if (file) this.readImage(file);
      e.target.value = ''
    },
    onDrop(e) {
      this.isDragging = false;
      const file = e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files[0];
      if (file) this.readImage(file)
    },
    readImage(file) {
      if (!file.type || file.type.indexOf('image/') !== 0) {
        this.$message.error('请选择 PNG、JPG、WebP 或 GIF 图片。');
        return
      }
      const url = URL.createObjectURL(file), image = new Image()
      image.onload = () => {
        if (this.sourceUrl) URL.revokeObjectURL(this.sourceUrl);
        this.sourceUrl = url;
        this.sourceImage = image;
        this.fileName = file.name;
        this.isSample = false;
        this.gridWidth = 32;
        this.maxColors = 24;
        this.zoom = 100;
        this.rebuildPattern()
      }
      image.onerror = () => {
        URL.revokeObjectURL(url);
        this.$message.error('这张图片暂时无法读取，请换一张试试。')
      }
      image.src = url
    },
    loadSample() {
      if (this.sourceUrl) URL.revokeObjectURL(this.sourceUrl)
      this.sourceUrl = '';
      this.sourceImage = null;
      this.isSample = true;
      this.fileName = '春日小花 · 示例';
      this.gridWidth = 18;
      this.maxColors = 12;
      this.zoom = 100;
      this.columns = 18;
      this.rows = 18;
      this.beadGrid = demoGrid();
      this.colorCounts = {'48': 0, '43': 0, '10': 0}
      this.beadGrid.forEach(row => row.forEach(code => {
        if (code) this.colorCounts[code]++
      }))
      this.$nextTick(this.drawBoard)
    },
    rebuildPattern() {
      if (!this.sourceImage) return
      let width = Math.max(12, Math.min(64, Number(this.gridWidth) || 32))
      let height = Math.max(1, Math.round(width * this.sourceImage.naturalHeight / this.sourceImage.naturalWidth))
      if (height > 96) {
        height = 96;
        width = Math.max(12, Math.round(height * this.sourceImage.naturalWidth / this.sourceImage.naturalHeight))
      }
      this.columns = width;
      this.rows = height;
      this.gridWidth = width

      const canvas = document.createElement('canvas');
      canvas.width = width;
      canvas.height = height
      const ctx = canvas.getContext('2d', {willReadFrequently: true});
      ctx.clearRect(0, 0, width, height);
      ctx.drawImage(this.sourceImage, 0, 0, width, height)
      const pixels = ctx.getImageData(0, 0, width, height).data
      const paletteRgb = {}
      this.palette.forEach(c => {
        paletteRgb[c.code] = rgb(c.hex)
      })

      // 第一步：所有像素匹配完整色板，统计频次
      const samples = []
      const frequency = {}
      for (let i = 0; i < width * height; i++) {
        if (pixels[i * 4 + 3] < 24) {
          samples.push(null);
          continue
        }
        const color = {r: pixels[i * 4], g: pixels[i * 4 + 1], b: pixels[i * 4 + 2]}
        let closest = this.palette[0], min = Infinity
        this.palette.forEach(c => {
          const d = distance(color, paletteRgb[c.code]);
          if (d < min) {
            min = d;
            closest = c
          }
        })
        samples.push({code: closest.code, rawColor: color});
        frequency[closest.code] = (frequency[closest.code] || 0) + 1
      }

      const limit = Math.max(1, Math.min(this.palette.length, Number(this.maxColors) || this.palette.length))
      // 选出出现最多的N个色号
      const selectedCodes = Object.keys(frequency).sort((a, b) => frequency[b] - frequency[a]).slice(0, limit)
      const allowedPalette = this.palette.filter(c => selectedCodes.includes(c.code))

      const counts = {}
      allowedPalette.forEach(c => {
        counts[c.code] = 0
      })

      this.beadGrid = Array.from({length: height}, (_, y) =>
          Array.from({length: width}, (_, x) => {
            const originalSample = samples[y * width + x];
            if (!originalSample) return null
            // ✅ 修复：直接拿原始像素rgb，不要再用paletteRgb[original]
            const pixelColor = originalSample.rawColor;
            let nearestCode = allowedPalette[0].code
            let minDist = Infinity
            allowedPalette.forEach(c => {
              const d = distance(pixelColor, paletteRgb[c.code])
              if (d < minDist) {
                minDist = d
                nearestCode = c.code
              }
            })
            counts[nearestCode]++;
            return nearestCode
          })
      )
      this.colorCounts = counts;
      this.isSample = false;
      this.$nextTick(this.drawBoard)
    },

    toggleCodes() {
      this.showCodes = !this.showCodes;
      this.drawBoard()
    },
    toggleGrid() {
      this.showGrid = !this.showGrid;
      this.drawBoard()
    },
    drawBoard() {
      if (this.$refs.boardCanvas && this.beadGrid.length) this.drawGrid(this.$refs.boardCanvas, this.beadGrid, 24, this.showCodes, this.showGrid, true, 38, 30)
    },
    drawGrid(canvas, grid, cell, codes, lines, coords, left, top) {
      const cols = grid[0] ? grid[0].length : 0, rows = grid.length
      canvas.width = left + cols * cell + 10;
      canvas.height = top + rows * cell + 10
      const ctx = canvas.getContext('2d');
      ctx.fillStyle = '#fff';
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      const lookup = {};
      this.palette.forEach(c => lookup[c.code] = c)
      grid.forEach((row, y) => row.forEach((code, x) => {
        if (!code || !lookup[code]) return
        const px = left + x * cell, py = top + y * cell, c = lookup[code];
        ctx.fillStyle = c.hex;
        ctx.fillRect(px, py, cell, cell)
        if (codes && cell >= 18) {
          const v = rgb(c.hex), light = (v.r * 299 + v.g * 587 + v.b * 114) / 1000 > 155;
          ctx.fillStyle = light ? 'rgba(48,48,48,.82)' : 'rgba(255,255,255,.94)';
          ctx.font = '700 ' + Math.max(8, Math.floor(cell * .36)) + 'px Arial';
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText(code, px + cell / 2, py + cell / 2)
        }
      }))
      if (lines) {
        for (let x = 0; x <= cols; x++) {
          const px = left + x * cell;
          ctx.beginPath();
          ctx.strokeStyle = x % 5 === 0 ? '#a5a79f' : '#dedfd9';
          ctx.lineWidth = x % 5 === 0 ? 1.2 : .65;
          ctx.moveTo(px, top);
          ctx.lineTo(px, top + rows * cell);
          ctx.stroke();
          if (coords && x > 0 && x % 5 === 0) {
            ctx.fillStyle = '#777b73';
            ctx.font = '600 9px Arial';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(String(x), px, top - 12)
          }
        }
        for (let y = 0; y <= rows; y++) {
          const py = top + y * cell;
          ctx.beginPath();
          ctx.strokeStyle = y % 5 === 0 ? '#a5a79f' : '#dedfd9';
          ctx.lineWidth = y % 5 === 0 ? 1.2 : .65;
          ctx.moveTo(left, py);
          ctx.lineTo(left + cols * cell, py);
          ctx.stroke();
          if (coords && y > 0 && y % 5 === 0) {
            ctx.fillStyle = '#777b73';
            ctx.font = '600 9px Arial';
            ctx.textAlign = 'right';
            ctx.textBaseline = 'middle';
            ctx.fillText(String(y), left - 8, py)
          }
        }
      } else {
        ctx.strokeStyle = '#dedfd9';
        ctx.strokeRect(left, top, cols * cell, rows * cell)
      }
    },
    makeReport() {
      const cols = this.beadGrid[0] ? this.beadGrid[0].length : 0, rows = this.beadGrid.length, cell = 25, left = 74,
          top = 175
      const width = Math.max(1120, left + cols * cell + 48), legendTop = top + rows * cell + 58, perRow = 3,
          legendRows = Math.ceil(this.usedColors.length / perRow),
          height = legendTop + Math.max(84, legendRows * 58) + 42
      const canvas = document.createElement('canvas');
      canvas.width = width;
      canvas.height = height
      const ctx = canvas.getContext('2d');
      ctx.fillStyle = '#fffdf9';
      ctx.fillRect(0, 0, width, height)
      ctx.fillStyle = '#344139';
      ctx.font = '700 34px Arial';
      ctx.fillText('BEAD STUDIO  /  拼豆图纸', left, 57)
      ctx.fillStyle = '#858a84';
      ctx.font = '17px Arial';
      ctx.fillText(this.fileName + '   ·   Hama MIDI   ·   ' + cols + ' × ' + rows + ' beads', left + 2, 96)
      ctx.fillStyle = '#e7e5de';
      ctx.fillRect(left, 122, width - left - 48, 1)
      this.drawReportGrid(ctx, left, top, cell)
      ctx.fillStyle = '#344139';
      ctx.font = '700 20px Arial';
      ctx.fillText('颜色用量  /  COLOR KEY', left, legendTop - 19)
      ctx.fillStyle = '#92958f';
      ctx.font = '14px Arial';
      ctx.textAlign = 'right';
      ctx.fillText('合计 ' + this.formatNumber(this.totalBeads) + ' 颗豆子', width - 48, legendTop - 19);
      ctx.textAlign = 'left'
      const cw = (width - left - 48) / perRow
      this.usedColors.forEach((c, i) => {
        const x = left + i % perRow * cw, y = legendTop + Math.floor(i / perRow) * 58;
        ctx.fillStyle = c.hex;
        ctx.fillRect(x, y, 32, 32);
        ctx.strokeStyle = 'rgba(0,0,0,.12)';
        ctx.strokeRect(x, y, 32, 32);
        ctx.fillStyle = '#38413a';
        ctx.font = '700 15px Arial';
        ctx.fillText(c.code + '  ' + c.name, x + 44, y + 13);
        ctx.fillStyle = '#8b908b';
        ctx.font = '13px Arial';
        ctx.fillText(c.english + ' · ' + this.formatNumber(this.colorCounts[c.code]) + ' 颗', x + 44, y + 29)
      })
      ctx.fillStyle = '#a6aaa2';
      ctx.font = '12px Arial';
      ctx.fillText('屏幕色块为近似显示，实物请以 Hama 官方色卡为准。', left, height - 15)
      return canvas
    },
    drawReportGrid(ctx, left, top, cell) {
      const cols = this.beadGrid[0].length, rows = this.beadGrid.length, lookup = {};
      this.palette.forEach(c => lookup[c.code] = c)
      this.beadGrid.forEach((row, y) => row.forEach((code, x) => {
        if (!code) return
        const px = left + x * cell, py = top + y * cell, c = lookup[code];
        ctx.fillStyle = c.hex;
        ctx.fillRect(px, py, cell, cell)
        const v = rgb(c.hex);
        ctx.fillStyle = (v.r * 299 + v.g * 587 + v.b * 114) / 1000 > 155 ? '#343434' : '#fff';
        ctx.font = '700 11px Arial';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(code, px + cell / 2, py + cell / 2)
      }))
      for (let x = 0; x <= cols; x++) {
        const px = left + x * cell;
        ctx.beginPath();
        ctx.strokeStyle = x % 5 === 0 ? '#898d87' : '#d7d8d3';
        ctx.lineWidth = x % 5 === 0 ? 1.3 : .7;
        ctx.moveTo(px, top);
        ctx.lineTo(px, top + rows * cell);
        ctx.stroke();
        if (x && x % 5 === 0) {
          ctx.fillStyle = '#666b65';
          ctx.font = '600 11px Arial';
          ctx.textAlign = 'center';
          ctx.fillText(String(x), px, top - 9)
        }
      }
      for (let y = 0; y <= rows; y++) {
        const py = top + y * cell;
        ctx.beginPath();
        ctx.strokeStyle = y % 5 === 0 ? '#898d87' : '#d7d8d3';
        ctx.lineWidth = y % 5 === 0 ? 1.3 : .7;
        ctx.moveTo(left, py);
        ctx.lineTo(left + cols * cell, py);
        ctx.stroke();
        if (y && y % 5 === 0) {
          ctx.fillStyle = '#666b65';
          ctx.font = '600 11px Arial';
          ctx.textAlign = 'right';
          ctx.fillText(String(y), left - 9, py)
        }
      }
      ctx.textAlign = 'left'
    },
    downloadPattern() {
      const canvas = this.makeReport(), link = document.createElement('a'),
          name = this.fileName.replace(/\.[^.]+$/, '').replace(/[\\/:*?"<>|]/g, '-') || '拼豆图纸'
      link.download = name + '-拼豆图纸.png';
      link.href = canvas.toDataURL('image/png');
      link.click();
      this.$message.success('图纸已下载为 PNG 图片。')
    },
    printPattern() {
      const popup = window.open('', '_blank');
      if (!popup) {
        this.$message.warning('请允许此站点打开弹窗后重试打印。');
        return
      }
      const url = this.makeReport().toDataURL('image/png')
      popup.document.open();
      popup.document.write('<!doctype html><html><head><meta charset="utf-8"><title>拼豆图纸</title><style>@page{size:A4 portrait;margin:8mm}*{box-sizing:border-box}html,body{margin:0;width:100%;height:100%;background:white}body{display:flex;align-items:center;justify-content:center}img{max-width:100%;max-height:280mm;object-fit:contain}@media screen{body{padding:20px;background:#f2f1ed}img{box-shadow:0 4px 24px #0002}}</style></head><body><img src="' + url + '" onload="window.print()" alt="拼豆图纸"></body></html>');
      popup.document.close()
    },
    quantityPercent(n) {
      const max = this.usedColors.reduce((m, c) => Math.max(m, this.colorCounts[c.code]), 1);
      return Math.max(4, Math.round(n / max * 100))
    },
    isLight(hex) {
      const c = rgb(hex);
      return (c.r * 299 + c.g * 587 + c.b * 114) / 1000 > 175
    },
    formatNumber(n) {
      return Number(n || 0).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.bead-studio {
  --ink: #344139;
  --muted: #92958c;
  --line: #eceae3;
  max-width: 1500px;
  margin: 0 auto;
  padding: 3px 2px 18px;
  color: var(--ink);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 3px 4px 22px
}

.brand {
  display: flex;
  align-items: center;
  gap: 11px
}

.brand > div:last-child {
  display: flex;
  flex-direction: column;
  gap: 3px
}

.brand b {
  font-size: 15px
}

.brand b small {
  margin-left: 5px;
  color: #9b9e96;
  font-size: 9px;
  letter-spacing: .13em
}

.brand span {
  color: #969890;
  font-size: 10px
}

.brand-icon {
  display: grid;
  width: 36px;
  height: 36px;
  grid-template-columns:repeat(3, 1fr);
  gap: 2px;
  padding: 5px;
  border-radius: 11px;
  background: #f6eee2;
  transform: rotate(-7deg)
}

.brand-icon i {
  border-radius: 50%;
  background: #d99183
}

.brand-icon i:nth-child(2), .brand-icon i:nth-child(6) {
  background: #e9c96d
}

.brand-icon i:nth-child(3), .brand-icon i:nth-child(7) {
  background: #88a98b
}

.brand-icon i:nth-child(4), .brand-icon i:nth-child(8) {
  background: #9dbdd0
}

.brand-icon i:nth-child(5) {
  background: #b39ac8
}

.guide-button {
  padding: 8px 13px;
  border: 1px solid #e8e7e1;
  border-radius: 18px;
  background: #fffefa;
  color: #68756b;
  font-size: 11px;
  cursor: pointer
}

.guide-button:hover {
  background: #f7faf6
}

.guide-button i {
  margin-right: 4px
}

.guide {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  margin: -10px 0 16px;
  padding: 12px 38px 12px 18px;
  border: 1px solid #e8eee7;
  border-radius: 10px;
  background: #f7faf5;
  color: #68756b;
  font-size: 11px
}

.guide b {
  margin-right: 6px;
  color: #91a793
}

.guide > i {
  color: #c3c9bf
}

.guide button {
  position: absolute;
  right: 11px;
  top: 7px;
  border: 0;
  background: none;
  color: #899186;
  font-size: 20px;
  cursor: pointer
}

.hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 180px;
  margin-bottom: 18px;
  padding: 25px 40px;
  overflow: hidden;
  border: 1px solid #f1ebe2;
  border-radius: 14px;
  background: #f8f4ec
}

.hero-copy {
  z-index: 1;
  max-width: 620px
}

.eyebrow, .panel-title small, .pattern-heading > div > small, .inventory-heading small {
  color: #a0a196;
  font-size: 8px;
  font-weight: 750;
  letter-spacing: .16em
}

.eyebrow {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 10px
}

.eyebrow i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #d58f7f
}

.hero h1 {
  margin: 0;
  color: #344139;
  font-size: clamp(24px, 3vw, 34px);
  letter-spacing: .04em
}

.hero h1 em {
  color: #cb887a;
  font-style: normal
}

.hero p {
  max-width: 590px;
  margin: 9px 0 14px;
  color: #7f837c;
  font-size: 11px;
  line-height: 1.8
}

.hero-points {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  color: #798177;
  font-size: 9px
}

.hero-points i {
  margin-right: 3px;
  color: #7fa186
}

.hero-art {
  position: relative;
  width: 195px;
  height: 142px;
  flex: 0 0 195px
}

.art-board {
  position: absolute;
  left: 32px;
  top: 15px;
  display: grid;
  width: 105px;
  height: 105px;
  grid-template-columns:repeat(3, 1fr);
  gap: 4px;
  padding: 10px;
  border: 1px solid #e9dfd0;
  border-radius: 9px;
  background: #fffefa;
  box-shadow: 0 8px 18px #705b451a;
  transform: rotate(6deg)
}

.art-board i {
  border-radius: 4px
}

.art-note {
  position: absolute;
  right: 0;
  bottom: 9px;
  color: #a5a292;
  font-size: 8px;
  font-weight: 700;
  letter-spacing: .1em;
  line-height: 1.6;
  transform: rotate(-6deg)
}

.art-spark {
  position: absolute;
  right: 25px;
  top: 0;
  color: #c6a676;
  font-size: 19px
}

.workbench {
  display: grid;
  grid-template-columns:minmax(235px, 285px) minmax(0, 1fr);
  gap: 15px;
  align-items: start
}

.settings-column {
  display: flex;
  flex-direction: column;
  gap: 12px
}

.panel {
  border: 1px solid #eeece6;
  border-radius: 12px;
  background: #fffefa;
  box-shadow: 0 2px 8px #3c3a3008
}

.settings-card {
  padding: 17px
}

.panel-title {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 13px
}

.panel-title small, .pattern-heading > div > small, .inventory-heading small {
  display: block;
  margin-bottom: 5px;
  font-size: 8px
}

.panel-title h2, .pattern-heading h2, .inventory-heading h2 {
  margin: 0;
  color: #38443b;
  font-size: 14px;
  font-weight: 720
}

.title-icon {
  display: flex;
  width: 29px;
  height: 29px;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  background: #f9eeea;
  color: #c58779;
  font-size: 15px
}

.title-icon.mint {
  background: #edf3ec;
  color: #78977f
}

.hidden-input {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0)
}

.dropzone {
  position: relative;
  display: flex;
  min-height: 133px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  overflow: hidden;
  border: 1px dashed #d9d8ce;
  border-radius: 9px;
  background: #fcfbf7;
  text-align: center;
  cursor: pointer;
  transition: .2s
}

.dropzone:hover, .dropzone.dragging {
  border-color: #9ab19d;
  background: #f5f8f3
}

.dropzone.has-image {
  min-height: 157px
}

.dropzone > img {
  width: 70px;
  height: 70px;
  margin-bottom: 8px;
  border: 1px solid #ebe8df;
  border-radius: 8px;
  object-fit: cover
}

.dropzone > b {
  color: #535e54;
  font-size: 11px
}

.dropzone > small {
  max-width: 100%;
  margin-top: 5px;
  overflow: hidden;
  color: #a09f96;
  font-size: 9px;
  text-overflow: ellipsis;
  white-space: nowrap
}

.upload-icon {
  margin-bottom: 9px;
  color: #889783;
  font-size: 25px
}

.replace-icon {
  position: absolute;
  top: 11px;
  right: 11px;
  color: #738675
}

.sample-link {
  display: flex;
  width: 100%;
  gap: 7px;
  margin-top: 11px;
  padding: 0;
  border: 0;
  background: none;
  color: #758c79;
  font-size: 10px;
  text-align: left;
  cursor: pointer
}

.sample-link span {
  margin-left: auto
}

.privacy-hint {
  margin-top: 10px;
  color: #8a9288;
  font-size: 9px
}

.privacy-hint i, .privacy-note i {
  margin-right: 3px;
  color: #91a48f
}

.palette-card {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 9px;
  border: 1px solid #efeee8;
  border-radius: 8px;
  background: #fff
}

.palette-mark {
  display: flex;
  width: 28px;
  height: 28px;
  align-items: center;
  justify-content: center;
  border-radius: 7px;
  background: #eef3ec;
  color: #64836d
}

.palette-card > span {
  display: flex;
  flex-direction: column;
  gap: 2px
}

.palette-card strong {
  color: #465147;
  font-size: 10px
}

.palette-card small {
  color: #989b93;
  font-size: 8px
}

.palette-card em {
  margin-left: auto;
  padding: 4px 6px;
  border-radius: 9px;
  background: #f6f3ea;
  color: #8b887d;
  font-size: 8px;
  font-style: normal
}

.palette-link {
  display: inline-block;
  margin: 7px 0 0;
  color: #8c9b89;
  font-size: 9px;
  text-decoration: none
}

.palette-link:hover {
  color: #55775e;
  text-decoration: underline
}

.range-setting {
  margin-top: 15px
}

.range-setting > div {
  display: flex;
  justify-content: space-between;
  color: #62695f;
  font-size: 10px
}

.range-setting > div b {
  color: #667d6b;
  font-size: 11px
}

.range-setting > div small {
  font-size: 8px;
  font-weight: 500
}

.range-setting input {
  display: block;
  width: 100%;
  height: 3px;
  margin: 10px 0 6px;
  appearance: none;
  border-radius: 3px;
  background: #e6ebe2;
  cursor: pointer
}

.range-setting input::-webkit-slider-thumb {
  width: 13px;
  height: 13px;
  appearance: none;
  border: 2px solid white;
  border-radius: 50%;
  background: #7f9c83;
  box-shadow: 0 0 0 1px #7f9c83
}

.range-setting input::-moz-range-thumb {
  width: 11px;
  height: 11px;
  border: 2px solid white;
  border-radius: 50%;
  background: #7f9c83;
  box-shadow: 0 0 0 1px #7f9c83
}

.range-setting input:disabled {
  opacity: .45;
  cursor: not-allowed
}

.range-setting footer {
  display: flex;
  justify-content: space-between;
  color: #b1b1a9;
  font-size: 8px
}

.control-hint {
  margin-top: 9px;
  color: #9a9f94;
  font-size: 8px
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
  padding-top: 11px;
  border-top: 1px solid #f1f0eb
}

.switch-row > span {
  display: flex;
  flex-direction: column;
  gap: 4px
}

.switch-row b {
  color: #636a60;
  font-size: 9px;
  font-weight: 650
}

.switch-row small {
  color: #a3a49c;
  font-size: 8px
}

.toggle {
  position: relative;
  width: 30px;
  height: 17px;
  padding: 0;
  border: 0;
  border-radius: 12px;
  background: #d9dbd4;
  cursor: pointer
}

.toggle i {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 13px;
  height: 13px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 1px 2px #0002;
  transition: .18s
}

.toggle.active {
  background: #83a18a
}

.toggle.active i {
  transform: translateX(13px)
}

.last-switch {
  margin-top: 11px
}

.privacy-note {
  padding: 1px 3px;
  color: #9b9d95;
  font-size: 8px;
  line-height: 1.6
}

.pattern-panel {
  min-width: 0;
  padding: 19px 20px 13px
}

.pattern-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px
}

.pattern-heading h2 {
  font-size: 16px
}

.pattern-heading h2 em, .inventory-heading h2 em {
  margin-left: 5px;
  padding: 3px 7px;
  border-radius: 9px;
  background: #f8eee8;
  color: #bb8478;
  font-size: 8px;
  font-style: normal;
  font-weight: 600
}

.pattern-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
  color: #999c94;
  font-size: 9px
}

.pattern-meta i {
  margin-right: 3px;
  color: #a6b29f
}

.actions {
  display: flex;
  gap: 6px
}

.actions button {
  display: flex;
  height: 32px;
  align-items: center;
  gap: 5px;
  padding: 0 9px;
  border-radius: 6px;
  font-size: 9px;
  font-weight: 600;
  cursor: pointer
}

.actions button:disabled {
  opacity: .45;
  cursor: not-allowed
}

.print-button {
  border: 1px solid #e5e7df;
  background: #fffefa;
  color: #66766a
}

.download-button {
  border: 1px solid #76947c;
  background: #76947c;
  color: #fff
}

.canvas-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 15px;
  padding: 8px 0;
  border-top: 1px solid #f0efe9;
  border-bottom: 1px solid #f0efe9;
  color: #858a81;
  font-size: 9px
}

.canvas-toolbar > span {
  display: flex;
  align-items: center;
  gap: 7px
}

.live-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #91aa8f;
  box-shadow: 0 0 0 3px #edf2ea
}

.zoom {
  display: flex;
  align-items: center;
  gap: 7px
}

.zoom button {
  width: 21px;
  height: 21px;
  border: 1px solid #eeede8;
  border-radius: 5px;
  background: white;
  color: #7c8379;
  cursor: pointer
}

.zoom span {
  min-width: 30px;
  text-align: center
}

.canvas-viewport {
  min-height: 315px;
  max-height: 800px;
  margin-top: 12px;
  overflow: auto;
  border-radius: 7px;
  background: #fbfaf6
}

.board-canvas {
  display: block;
  height: auto;
  max-width: none;
  margin: 14px auto
}

.empty-board {
  display: flex;
  min-height: 315px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9b9e96
}

.empty-board > i {
  margin-bottom: 12px;
  color: #aab6a4;
  font-size: 30px
}

.empty-board b {
  color: #626b60;
  font-size: 11px
}

.empty-board span {
  margin-top: 6px;
  font-size: 9px
}

.empty-board button {
  margin-top: 12px;
  padding: 7px 10px;
  border: 1px solid #dce4d8;
  border-radius: 6px;
  background: #fff;
  color: #6d886e;
  font-size: 9px;
  cursor: pointer
}

.board-footer {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  padding-top: 11px;
  color: #a2a39c;
  font-size: 8px
}

.board-footer i {
  margin-right: 3px;
  color: #c5aa7a
}

.board-footer span:last-child {
  white-space: nowrap
}

.inventory-panel {
  margin-top: 15px;
  padding: 17px 20px 13px
}

.inventory-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 13px
}

.inventory-heading h2 {
  font-size: 14px
}

.inventory-heading h2 em {
  background: #f2f4ee;
  color: #84927f
}

.inventory-heading p {
  margin: 0 0 2px;
  color: #a0a199;
  font-size: 8px
}

.color-list {
  display: grid;
  grid-template-columns:repeat(3, minmax(0, 1fr));
  gap: 7px
}

.color-item {
  position: relative;
  display: flex;
  min-width: 0;
  min-height: 50px;
  align-items: center;
  gap: 7px;
  padding: 7px;
  border: 1px solid #f0efe9;
  border-radius: 7px;
  background: #fff
}

.swatch {
  display: flex;
  width: 28px;
  height: 28px;
  flex: 0 0 28px;
  align-items: center;
  justify-content: center;
  border: 1px solid #282d2814;
  border-radius: 6px;
  color: #555;
  font-size: 7px;
  font-weight: 700
}

.color-name {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 3px;
  overflow: hidden
}

.color-name b, .color-name small {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap
}

.color-name b {
  color: #555e54;
  font-size: 9px
}

.color-name b span {
  margin-left: 2px;
  color: #737b71;
  font-weight: 500
}

.color-name small {
  color: #a5a69e;
  font-size: 7px
}

.quantity {
  margin-left: auto;
  color: #798274;
  font-size: 11px;
  white-space: nowrap
}

.quantity small {
  margin-left: 2px;
  color: #a7a79f;
  font-size: 7px;
  font-weight: 400
}

.quantity-track {
  position: absolute;
  right: 7px;
  bottom: 3px;
  left: 43px;
  height: 2px;
  overflow: hidden;
  border-radius: 2px;
  background: #f0f0eb
}

.quantity-track i {
  display: block;
  height: 100%;
  min-width: 2px;
  opacity: .8
}

.inventory-empty {
  padding: 20px 4px;
  color: #a3a49c;
  font-size: 9px;
  text-align: center
}

.inventory-empty i {
  margin-right: 5px;
  color: #c3ab82
}

.inventory-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid #f0efe9;
  color: #9fa198;
  font-size: 8px
}

.inventory-footer b {
  color: #777f73;
  font-size: 9px
}

.inventory-footer strong {
  margin: 0 3px;
  color: #55795f;
  font-size: 12px
}

.studio-footer {
  display: flex;
  justify-content: space-between;
  padding: 17px 5px 0;
  color: #a6a79f;
  font-size: 7px;
  letter-spacing: .1em
}

.studio-footer span:last-child {
  letter-spacing: .02em
}

.studio-footer i {
  color: #cc9a87;
  font-style: normal
}

@media (max-width: 1000px) {
  .hero {
    padding-right: 24px;
    padding-left: 27px
  }

  .hero-art {
    transform: scale(.85);
    transform-origin: right center
  }

  .workbench {
    grid-template-columns:minmax(220px, 260px) minmax(0, 1fr);
    gap: 11px
  }

  .pattern-panel {
    padding-right: 14px;
    padding-left: 14px
  }

  .color-list {
    grid-template-columns:repeat(2, minmax(0, 1fr))
  }
}

@media (max-width: 760px) {
  .hero {
    min-height: 0;
    padding: 22px 18px
  }

  .hero-art {
    position: absolute;
    right: -30px;
    bottom: -10px;
    opacity: .35;
    transform: scale(.8)
  }

  .hero-copy {
    max-width: 100%
  }

  .hero-points {
    gap: 8px 12px
  }

  .workbench {
    grid-template-columns:1fr
  }

  .settings-column {
    display: grid;
    grid-template-columns:repeat(2, minmax(0, 1fr));
    align-items: start
  }

  .privacy-note {
    grid-column: 1/-1
  }

  .pattern-panel {
    padding: 16px 13px 12px
  }

  .pattern-meta {
    gap: 7px;
    font-size: 8px
  }

  .actions {
    flex-direction: column
  }

  .actions button {
    height: 28px;
    justify-content: center
  }

  .canvas-viewport {
    min-height: 250px
  }

  .guide {
    flex-wrap: wrap;
    gap: 8px 12px
  }

  .guide > i {
    display: none
  }
}

@media (max-width: 520px) {
  .brand b small {
    display: none
  }

  .brand span {
    font-size: 9px
  }

  .hero {
    margin-bottom: 12px
  }

  .hero p {
    font-size: 10px
  }

  .hero-points {
    max-width: 270px
  }

  .hero-points span {
    font-size: 8px
  }

  .settings-column {
    grid-template-columns:1fr
  }

  .pattern-heading h2 {
    font-size: 14px
  }

  .pattern-meta {
    max-width: 250px
  }

  .actions button {
    width: 32px;
    padding: 0
  }

  .actions button span {
    display: none
  }

  .board-footer {
    align-items: flex-start;
    font-size: 7px
  }

  .inventory-panel {
    padding-right: 11px;
    padding-left: 11px
  }

  .inventory-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 5px
  }

  .color-list {
    grid-template-columns:1fr
  }

  .studio-footer {
    gap: 8px;
    font-size: 6px
  }

  .studio-footer span:first-child {
    letter-spacing: .04em
  }
}
</style>
