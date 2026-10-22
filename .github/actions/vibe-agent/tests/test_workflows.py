"""Exercise the trusted-checkout preflight without running a GitHub action."""
from pathlib import Path
import os
import subprocess
import tempfile
import textwrap
import unittest

ROOT = Path(__file__).resolve().parents[4]


class TrustedCheckoutTest(unittest.TestCase):
    def test_missing_default_branch_action_skips_and_installed_action_runs(self):
        for mode in ['review', 'ci-monitor']:
            source = (ROOT / f'.github/workflows/vibe-agent-{mode}.yml').read_text()
            preflight = source.index('      - name: Check trusted agent availability')
            start = source.index('        run: |\n', preflight) + len('        run: |\n')
            end = source.index('      - name: Run Vibe Agent', start)
            script = textwrap.dedent(source[start:end])
            self.assertLess(source.index('uses: actions/checkout@'), preflight)
            self.assertIn('ref: ${{ github.event.repository.default_branch }}', source[:preflight])
            self.assertIn("if: steps.agent.outputs.available == 'true'", source[end:])
            for installed in [False, True]:
                with self.subTest(mode=mode, installed=installed), tempfile.TemporaryDirectory() as directory:
                    root = Path(directory)
                    output = root / 'outputs'
                    if installed:
                        manifest = root / '.github/actions/vibe-agent/action.yml'
                        manifest.parent.mkdir(parents=True)
                        manifest.write_text('name: Test action\n')
                    result = subprocess.run(['bash', '-euo', 'pipefail', '-c', script], cwd=root,
                                            env=dict(os.environ, GITHUB_OUTPUT=str(output)),
                                            text=True, capture_output=True)
                    self.assertEqual(result.returncode, 0, result.stderr)
                    self.assertEqual(output.read_text().strip(), f'available={str(installed).lower()}')
                    if not installed:
                        self.assertIn('not installed on the default branch', result.stdout)
